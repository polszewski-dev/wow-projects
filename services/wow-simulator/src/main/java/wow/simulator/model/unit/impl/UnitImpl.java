package wow.simulator.model.unit.impl;

import lombok.RequiredArgsConstructor;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.ItemSockets;
import wow.character.model.script.ScriptPathResolver;
import wow.character.model.snapshot.*;
import wow.character.model.talent.Talents;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.character.*;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.ItemSet;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.*;
import wow.commons.model.talent.TalentTree;
import wow.simulator.model.context.Context;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.Cooldowns;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.Effects;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.*;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.IdleAction;
import wow.simulator.model.unit.action.ImmediateAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.script.ScriptExecutor;
import wow.simulator.script.ScriptParams;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextAware;
import wow.simulator.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static wow.commons.model.spell.GcdCooldownId.GCD;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.spell.component.ComponentCommand.*;
import static wow.simulator.model.time.AnyTime.TIME_IN_INFINITY;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public abstract class UnitImpl extends CharacterImpl implements Unit, SimulationContextAware {
	private static final IdGenerator<UnitId> ID_GENERATOR = new IdGenerator<>(UnitId::new);

	protected final UnitId id = ID_GENERATOR.newId();

	private final UnitResources resources = new UnitResources(this);
	protected final Effects effects = new UnitEffects(this);
	private final Cooldowns cooldowns = new Cooldowns(this);

	private UnitState state;

	private final PendingActionQueue<UnitAction> pendingActionQueue = new PendingActionQueue<>();
	private UnitAction currentAction;

	private Rng rng;

	private SimulationContext simulationContext;

	private Time lastTimeManaSpent;

	private boolean deactivated;
	private Consumer<Unit> onDeath;

	private boolean inCombat;

	protected UnitImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, talents);
		getEquipment().setOnEquipmentChanged(this::onEquipmentChanged);
		this.state = new PassiveState();
		this.resources.setHealth(10_000, 10_000);
		this.resources.setMana(10_000, 10_000);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	@Override
	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		shareClock(effects);
		shareClock(cooldowns);
	}

	private void ensureAction() {
		if (getSimulation().isFinished()) {
			return;
		}

		if (hasActionInProgress() || isOnCooldown(GCD)) {
			return;
		}

		if (deactivated) {
			return;
		}

		if (pendingActionQueue.isEmpty()) {
			state.onPendingActionQueueEmpty();

			if (hasActionInProgress()) {
				return;
			}

			if (pendingActionQueue.isEmpty()) {
				throw new IllegalStateException();
			}
		}

		var newAction = pendingActionQueue.removeEarliestAction();

		startAction(newAction);
	}

	private void startAction(UnitAction newAction) {
		this.currentAction = newAction;
		getScheduler().add(newAction);
	}

	private void enqueueAction(UnitAction action) {
		if (pendingActionQueue.isEmpty() && currentAction == null) {
			startAction(action);
		} else {
			pendingActionQueue.add(action);
			state.onActionEnqueued();
		}
	}

	@Override
	public UnitId getId() {
		return id;
	}

	@Override
	public StatSummary getStats() {
		return getCharacterCalculationService().getStatSummary(this);
	}

	@Override
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}

	@Override
	public void setTarget(Unit target) {
		setTarget((Character) target);
	}

	@Override
	public Unit getFocus() {
		return (Unit) super.getFocus();
	}

	@Override
	public void setFocus(Unit focus) {
		setFocus((Character) focus);
	}

	@Override
	public void setActive() {
		state.setActive();
	}

	@Override
	public void setPassive() {
		state.setPassive();
	}

	@Override
	public void cast(String abilityName) {
		cast(abilityName, null);
	}

	@Override
	public void cast(String abilityName, Unit target) {
		var abilityId = AbilityId.of(abilityName);
		cast(abilityId, target);
	}

	@Override
	public void cast(AbilityId abilityId) {
		cast(abilityId, null);
	}

	@Override
	public void cast(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		cast(ability, target);
	}

	@Override
	public void cast(Ability ability, Unit target) {
		var action = new CastSpellAction(this, ability, target);

		enqueueAction(action);
	}

	@Override
	public void petCast(String abilityName) {
		immediateAction(() -> getActivePet().cast(abilityName));
	}

	@Override
	public void petCast(String abilityName, Unit target) {
		immediateAction(() -> getActivePet().cast(abilityName, target));
	}

	@Override
	public void petCast(Ability ability, Unit target) {
		immediateAction(() -> getActivePet().cast(ability, target));
	}

	@Override
	public void petCast(Supplier<Ability> abilitySupplier, Supplier<Unit> targetSupplier) {
		immediateAction(() -> {
			var ability = abilitySupplier.get();
			var target = targetSupplier.get();

			getActivePet().cast(ability, target);
		});
	}

	@Override
	public PrimaryTarget getPrimaryTarget(Ability ability, Unit explicitTarget) {
		var resolver = new PrimaryTargetResolver(ability, this, getTarget(), explicitTarget);
		return resolver.getPrimaryTarget();
	}

	@Override
	public void idleUntil(AnyTime time) {
		enqueueAction(new IdleAction(this, time));
	}

	@Override
	public void idleFor(Duration duration) {
		idleUntil(now().add(duration));
	}

	@Override
	public void immediateAction(Consumer<Unit> action) {
		enqueueAction(new ImmediateAction(this, action));
	}

	@Override
	public void immediateAction(Runnable action) {
		immediateAction(self -> action.run());
	}

	@Override
	public void emptyAction() {
		immediateAction(() -> {
			// do nothing
		});
	}

	@Override
	public void triggerGcd(Duration duration) {
		triggerCooldown(GCD, duration);
	}

	@Override
	public void interruptCurrentAction() {
		if (currentAction == null || !isCurrentActionInterruptible()) {
			return;
		}

		var triggersGcd = currentAction.triggersGcd();

		currentAction.interrupt();

		if (triggersGcd) {
			cooldowns.interruptGcd();
		}
	}

	private void interruptCurrentActionIfIdle() {
		if (currentAction instanceof IdleAction idleAction && idleAction.endIsInInfinity()) {
			interruptCurrentAction();
		}
	}

	private boolean isCurrentActionInterruptible() {
		if (hasActionInProgress()) {
			return true;
		}
		return !cooldowns.isOnCooldown(GCD);
	}

	private boolean hasActionInProgress() {
		return currentAction != null && currentAction.isInProgress();
	}

	@Override
	public boolean canCast(String abilityName) {
		return canCast(abilityName, null);
	}

	@Override
	public boolean canCast(String abilityName, Unit target) {
		var abilityId = AbilityId.of(abilityName);
		return canCast(abilityId, target);
	}

	@Override
	public boolean canCast(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		var primaryTarget = getPrimaryTarget(ability, target);
		return canCast(ability, primaryTarget);
	}

	@Override
	public boolean canCast(Ability ability, Unit target) {
		var primaryTarget = getPrimaryTarget(ability, target);
		return canCast(ability, primaryTarget);
	}

	@Override
	public boolean canCast(Ability ability, PrimaryTarget primaryTarget) {
		return hasAllValidTargets(ability, primaryTarget.getTargetResolver(this)) &&
				!isOnCooldown(ability) &&
				canPaySpellCost(ability, primaryTarget) &&
				!isSchoolPrevented(ability.getSchool());
	}

	private boolean hasAllValidTargets(Ability ability, TargetResolver targetResolver) {
		if (targetResolver == null) {
			return false;
		}
		var targets = ability.getTargets();
		return targetResolver.hasAllValidTargets(targets);
	}

	private boolean canPaySpellCost(Ability ability, PrimaryTarget primaryTarget) {
		if (ability instanceof ActivatedAbility) {
			return true;
		}
		var costSnapshot = getSpellCostSnapshot(ability, primaryTarget.getSingleTarget());
		return getResources().canPay(costSnapshot.getCostToPay());
	}

	private boolean isSchoolPrevented(SpellSchool school) {
		return effects.getStream().anyMatch(x -> x.isSchoolPrevented(school));
	}

	@Override
	public SpellCostSnapshot paySpellCost(Ability ability, PrimaryTarget primaryTarget, Context parentContext) {
		var costSnapshot = getSpellCostSnapshot(ability, primaryTarget.getSingleTarget());
		var cost = costSnapshot.getCostToPay();

		getResources().pay(cost);

		var actualAmount = cost.amount();

		if (actualAmount > 0) {
			var type = cost.resourceType();

			getGameLog().decreasedResource(type, ability, this, actualAmount, true, false, this);
		}

		if (cost.resourceType() == MANA && cost.amount() > 0) {
			this.lastTimeManaSpent = now();
		}

		return costSnapshot;
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		return getSpellCastSnapshot(ability, target);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Ability ability, Unit target) {
		return getCharacterCalculationService().getSpellCastSnapshot(this, ability, target);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Ability ability, Unit target) {
		return getCharacterCalculationService().getSpellCostSnapshot(this, ability, target);
	}

	@Override
	public double getSpellHitPct(Spell spell, Unit target) {
		return getCharacterCalculationService().getSpellHitPct(this, spell, target);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		var command = ability.getApplyEffectCommands().getFirst();

		return getEffectDurationSnapshot(ability, target, command);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Spell spell, Unit target) {
		var command = spell.getApplyEffectCommands().getFirst();

		return getEffectDurationSnapshot(spell, target, command);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Spell spell, Unit target, ApplyEffect command) {
		return getCharacterCalculationService().getEffectDurationSnapshot(this, spell, target, command);
	}

	@Override
	public AnyDuration getSummonDuration(Spell spell, AnyDuration commandDuration) {
		return getCharacterCalculationService().getSummonDuration(this, spell, commandDuration);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Spell spell, Unit target, DealDamageDirectly command) {
		return getCharacterCalculationService().getDirectSpellDamageSnapshot(this, spell, target, command);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectHealingSnapshot(Spell spell, Unit target, HealDirectly command) {
		return getCharacterCalculationService().getDirectHealingSnapshot(this, spell, target, command);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Spell spell, Unit target, DealDamagePeriodically command) {
		return getCharacterCalculationService().getPeriodicSpellDamageSnapshot(this, spell, target, command);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Spell spell, Unit target, HealPeriodically command) {
		return getCharacterCalculationService().getPeriodicHealingSnapshot(this, spell, target, command);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicManaLossSnapshot(Spell spell, Unit target, LoseManaPeriodically command) {
		return new PeriodicSpellComponentSnapshot(command.amount(), command.numTicks());
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicManaGainSnapshot(Spell spell, Unit target, GainManaPeriodically command) {
		return new PeriodicSpellComponentSnapshot(command.amount(), command.numTicks());
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicPctOfTotalManaGainSnapshot(Spell spell, Unit target, GainPctOfTotalManaPeriodically command) {
		var pct = command.amount();
		var maxMana = target.getMaxMana();

		return new PeriodicSpellComponentSnapshot(maxMana * pct / 100, command.numTicks());
	}

	@Override
	public Rng getRng() {
		if (rng == null) {
			rng = getRngFactory().newRng();
		}
		return rng;
	}

	@Override
	public int getCurrentHealth() {
		return getResources().getCurrentHealth();
	}

	@Override
	public int getCurrentMana() {
		return getResources().getCurrentMana();
	}

	@Override
	public int getMaxHealth() {
		return getResources().getMaxHealth();
	}

	@Override
	public int getMaxMana() {
		return getResources().getMaxMana();
	}

	@Override
	public Percent getHealthPct() {
		return getResources().getHealthPercent();
	}

	@Override
	public Percent getManaPct() {
		return getResources().getManaPercent();
	}

	@Override
	public void setCurrentHealth(int amount) {
		getResources().setHealth(amount, getMaxHealth());
	}

	@Override
	public void setCurrentMana(int amount) {
		getResources().setMana(amount, getMaxMana());
	}

	@Override
	public void setHealthToMax() {
		getResources().setHealthToMax();
	}

	@Override
	public void setManaToMax() {
		getResources().setManaToMax();
	}

	@Override
	public void setAllResourcesToMax() {
		getResources().setAllToMax();
	}

	@Override
	public int increaseHealth(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		int actualAmount = getResources().increaseHealth(amount);

		if (actualAmount > 0) {
			getGameLog().increasedResource(HEALTH, spell, this, actualAmount, true, crit, caster);
		}

		return actualAmount;
	}

	@Override
	public int decreaseHealth(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		putInCombat((UnitImpl) caster, this);
		int actualAmount = getResources().decreaseHealth(amount);

		if (actualAmount > 0) {
			getGameLog().decreasedResource(HEALTH, spell, this, actualAmount, true, crit, caster);

			if (getCurrentHealth() == 0) {
				getGameLog().targetDied(this, caster);
				triggerDeath(caster);
			}
		}

		return actualAmount;
	}

	@Override
	public int increaseMana(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		int actualAmount = getResources().increaseMana(amount);

		if (actualAmount > 0) {
			getGameLog().increasedResource(MANA, spell, this, actualAmount, true, crit, caster);
		}

		return actualAmount;
	}

	@Override
	public int decreaseMana(int amount, boolean direct, boolean crit, Unit caster, Spell spell, Context parentContext) {
		putInCombat((UnitImpl) caster, this);
		int actualAmount = getResources().decreaseMana(amount);

		if (actualAmount > 0) {
			getGameLog().decreasedResource(MANA, spell, this, actualAmount, true, crit, caster);
		}

		return actualAmount;
	}

	@Override
	public void addEffect(EffectInstance effect, EffectReplacementMode replacementMode) {
		putInCombat((UnitImpl) effect.getOwner(), this);
		effects.addEffect(effect, replacementMode);
	}

	@Override
	public void addHiddenEffect(String effectName, int numStacks) {
		addHiddenEffect(effectName, numStacks, Duration.INFINITE, null);
	}

	@Override
	public void addHiddenEffect(String effectName, int numStacks, AnyDuration duration) {
		addHiddenEffect(effectName, numStacks, duration, null);
	}

	@Override
	public void addHiddenEffect(String effectName, int numStacks, AnyDuration duration, Spell sourceSpell) {
		var effect = getSpellRepository().getEffect(effectName, getPhaseId()).orElseThrow();
		var effectInstance = new NonPeriodicEffectInstance(
				this,
				this,
				effect,
				duration,
				numStacks,
				1,
				0,
				null,
				sourceSpell,
				null
		);

		effectInstance.setHidden(true);
		addEffect(effectInstance, EffectReplacementMode.DEFAULT);
	}

	@Override
	public void removeEffect(EffectInstance effect) {
		effects.removeEffect(effect);
	}

	@Override
	public void removeEffect(AbilityId abilityId, Unit owner) {
		effects.removeEffect(abilityId, owner);
	}

	@Override
	public void removeEffect(String effectName) {
		effects.removeEffect(effectName);
	}

	@Override
	public Optional<EffectInstance> getEffect(AbilityId abilityId, Unit owner) {
		return effects.getEffect(abilityId, owner);
	}

	@Override
	public Optional<EffectInstance> getEffect(String effectName) {
		return effects.getEffect(effectName);
	}

	@Override
	public Optional<EffectInstance> getEffect(String effectName, Unit effectOwner) {
		return effects.getEffect(effectName, effectOwner);
	}

	@Override
	public boolean hasEffect(AbilityId requiredEffect, Unit effectOwner) {
		return effects.isUnderEffect(requiredEffect, effectOwner);
	}

	@Override
	public boolean hasEffect(String effectName) {
		return effects.isUnderEffect(effectName);
	}

	@Override
	public boolean hasEffect(Pattern effectNamePattern) {
		return effects.isUnderEffect(effectNamePattern);
	}

	@Override
	public boolean hasEffect(Pattern effectNamePattern, Unit effectOwner) {
		return effects.isUnderEffect(effectNamePattern, effectOwner);
	}

	@Override
	public Optional<AnyDuration> getRemainingEffectDuration(String effectName, Unit caster) {
		return getEffect(effectName, caster)
				.map(EffectInstance::getRemainingDuration);
	}

	@Override
	public Optional<AnyDuration> getRemainingEffectDuration(Ability ability, Unit caster) {
		return getEffect(ability.getAbilityId(), caster)
				.map(EffectInstance::getRemainingDuration);
	}

	@Override
	public int getNumberOfEffects(TalentTree tree) {
		return effects.getNumberOfEffects(tree);
	}

	@Override
	public boolean isOnCooldown(AbilityId abilityId) {
		return cooldowns.isOnCooldown(abilityId);
	}

	@Override
	public boolean isOnCooldown(Ability ability) {
		if (isOnCooldown(ability.getAbilityId())) {
			return true;
		}

		if (ability instanceof ActivatedAbility activatedAbility) {
			var groupCooldownId = activatedAbility.getGroupCooldownId();
			return groupCooldownId != null && isOnCooldown(groupCooldownId);
		}

		return false;
	}

	@Override
	public boolean isOnCooldown(CooldownId cooldownId) {
		return cooldowns.isOnCooldown(cooldownId);
	}

	@Override
	public Duration getRemainingCooldown(AbilityId abilityId) {
		return cooldowns.getRemainingCooldown(abilityId);
	}

	@Override
	public void triggerCooldown(Ability ability, Duration actualDuration) {
		cooldowns.triggerCooldown(ability, actualDuration, currentAction);
	}

	@Override
	public void triggerCooldown(CooldownId cooldownId, Duration actualDuration) {
		cooldowns.triggerCooldown(cooldownId, actualDuration, currentAction);
	}

	@Override
	public void regen(Duration sinceLastRegen) {
		var snapshot = getCharacterCalculationService().getRegenSnapshot(this);
		var sinceLastManaSpent = getSinceLastManaSpent();
		var health = snapshot.getHealthToRegen(true, sinceLastRegen);
		var mana = snapshot.getManaToRegen(sinceLastManaSpent, sinceLastRegen);

		increaseHealth(health, false, false, null, null, null);
		increaseMana(mana, false, false, null, null, null);
	}

	private Duration getSinceLastManaSpent() {
		return lastTimeManaSpent != null ? now().subtract(lastTimeManaSpent) : null;
	}

	// character interface

	@Override
	public void setTarget(Character target) {
		if (target != null && !(target instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
	}

	@Override
	public void setFocus(Character focus) {
		if (focus != null && !(focus instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setFocus(focus);
	}

	@Override
	public void setHealthPct(Percent healthPct) {
		throw new UnsupportedOperationException();
	}

	public void detach(CooldownInstance cooldown) {
		cooldowns.detach(cooldown);

		if (cooldown.getCooldownId() == GCD) {
			ensureAction();
		}
	}

	public void detach(EffectInstance effect) {
		effects.detach(effect);
	}

	public void actionTerminated(UnitAction expectedCurrentAction) {
		if (currentAction != expectedCurrentAction) {
			return;
		}

		this.currentAction = null;
		ensureAction();
	}

	public void replaceCurrentAction(UnitAction newAction) {
		startAction(newAction);
	}

	@Override
	public void deactivate() {
		if (deactivated) {
			return;
		}

		this.deactivated = true;
		invalidateAuras();
	}

	@Override
	public boolean isAlive() {
		return getCurrentHealth() > 0;
	}

	@Override
	public boolean isDead() {
		return !isAlive();
	}

	@Override
	public void triggerDeath(Unit caster) {
		deactivate();
		effects.removeAllEffects();
		dismissPet();
		if (onDeath != null) {
			onDeath.accept(this);
		}
	}

	@Override
	public void setOnDeath(Consumer<Unit> onDeath) {
		this.onDeath = onDeath;
	}

	protected UnitResources getResources() {
		if (resourcesNeedRefresh) {
			refreshResources();
			resourcesNeedRefresh = false;
		}

		return resources;
	}

	private void refreshResources() {
		var stats = getStats();
		var maxHealth = stats.getMaxHealth();
		var maxMana = stats.getMaxMana();
		var currentHealth = resources.getCurrentHealth();
		var currentMana = resources.getCurrentMana();

		resources.setHealth(currentHealth, maxHealth);
		resources.setMana(currentMana, maxMana);
	}

	private boolean resourcesNeedRefresh = true;

	@Override
	public Pet getActivePet() {
		return (Pet) super.getActivePet();
	}

	@Override
	public void summonPet(PetType petType, Spell sourceSpell) {
		dismissPet();

		var petName = "%s's %s".formatted(getName(), petType.getName());
		var pet = getCharacterService().createPetCharacter(petName, petType, this, sourceSpell, PetImpl::new);

		getCharacterService().applyDefaultCharacterTemplate(pet);

		this.setActivePet(pet);

		if (inCombat) {
			pet.setActive();
		}

		getSimulation().add(pet);
	}

	@Override
	public Pet dismissPet() {
		return cleanUpAfterPetIsGone();
	}

	@Override
	public Pet unsummonPet() {
		return cleanUpAfterPetIsGone();
	}

	@Override
	public Pet sacrificePet() {
		if (getActivePet() == null) {
			throw new IllegalStateException("No active pet for the sacrifice");
		}

		return cleanUpAfterPetIsGone();
	}

	private Pet cleanUpAfterPetIsGone() {
		var activePet = getActivePet();

		if (activePet == null) {
			return null;
		}

		activePet.deactivate();
		this.setActivePet(null);
		getSimulation().remove(activePet);

		return activePet;
	}

	private static void putInCombat(UnitImpl caster, UnitImpl target) {
		if (target.inCombat || caster.isHostileWith(target)) {
			caster.putInCombat();
			target.putInCombat();
		}
	}

	private void putInCombat() {
		this.inCombat = true;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		if (deactivated) {
			return;
		}

		var auraExcludingCollector = new AuraExcludingCollector(collector);

		super.collectEffects(auraExcludingCollector);
		effects.collectEffects(auraExcludingCollector);
		getParty().getAuras().collectEffects(collector);
	}

	@Override
	public void collectAuras(EffectCollector collector) {
		if (deactivated) {
			return;
		}

		getEquipment().collectEffects(collector);

		for (var racial : getRacials()) {
			collector.addEffect(racial);
		}

		effects.collectEffects(collector);
	}

	@Override
	public List<Unit> getAllEnemies() {
		return getSimulation().getEnemiesOf(this);
	}

	@Override
	public List<Unit> getAllFriends() {
		return getSimulation().getFriendsOf(this);
	}

	@Override
	public void onAddedToSimulation() {
		setAllResourcesToMax();
		getSimulation().delayedAction(Duration.ZERO, this::ensureAction);
	}

	@Override
	public void onEffectListChanged(EffectInstance effectInstance) {
		if (effectInstance.hasResourceModifier()) {
			resourcesNeedRefresh = true;
		}

		if (effectInstance.isAura()) {
			invalidateAuras();
		}
	}

	private void onEquipmentChanged() {
		resourcesNeedRefresh = true;
		invalidateAuras();
	}

	private void invalidateAuras() {
		var party = getParty();

		if (party != null) {
			party.invalidateAuras();
		}
	}

	@Override
	public FormType getForm() {
		return effects.getForm();
	}

	@Override
	public void resetAfterCombat() {
		unsummonPet();
		effects.reset();
		cooldowns.reset();
		pendingActionQueue.reset();
		currentAction = null;
		deactivated = false;
		lastTimeManaSpent = null;
		inCombat = false;
		setPassive();
		setAllResourcesToMax();
	}

	@RequiredArgsConstructor
	private static class AuraExcludingCollector implements EffectCollector {
		private final EffectCollector collector;

		@Override
		public void addEffect(Effect effect, int stackCount) {
			if (effect.isAura() && !effect.hasAugmentedAbilities()) {
				return;
			}
			collector.addEffect(effect, stackCount);
		}

		@Override
		public void addActivatedAbility(ActivatedAbility activatedAbility) {
			collector.addActivatedAbility(activatedAbility);
		}

		@Override
		public void addItemSockets(ItemSockets itemSockets) {
			collector.addItemSockets(itemSockets);
		}

		@Override
		public void addItemSet(ItemSet itemSet) {
			collector.addItemSet(itemSet);
		}
	}

	private interface UnitState {
		void onPendingActionQueueEmpty();

		void onActionEnqueued();

		void setActive();

		void setPassive();
	}

	private class ActiveState implements UnitState {
		private final ScriptExecutor scriptExecutor;

		ActiveState() {
			var scriptPath = ScriptPathResolver.getScriptPath(UnitImpl.this);
			var params = new ScriptParams(UnitImpl.this);

			this.scriptExecutor = new ScriptExecutor(scriptPath, params);
		}

		@Override
		public void onPendingActionQueueEmpty() {
			scriptExecutor.execute();
		}

		@Override
		public void onActionEnqueued() {
			// void
		}

		@Override
		public void setActive() {
			// void
		}

		@Override
		public void setPassive() {
			UnitImpl.this.state = new PassiveState();
			interruptCurrentAction();
		}
	}

	private class PassiveState implements UnitState {
		@Override
		public void onPendingActionQueueEmpty() {
			idleUntil(TIME_IN_INFINITY);
		}

		@Override
		public void onActionEnqueued() {
			interruptCurrentActionIfIdle();
		}

		@Override
		public void setActive() {
			UnitImpl.this.state = new ActiveState();
			interruptCurrentAction();
		}

		@Override
		public void setPassive() {
			// void
		}
	}
}
