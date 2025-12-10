package wow.simulator.model.unit.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.snapshot.*;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Pet;
import wow.commons.model.character.PetType;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.*;
import wow.commons.model.talent.TalentTree;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.Cooldowns;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.Effects;
import wow.simulator.model.effect.impl.NonPeriodicEffectInstance;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.unit.*;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.IdleAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextAware;
import wow.simulator.util.IdGenerator;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static wow.commons.model.spell.GcdCooldownId.GCD;
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

	private final PendingActionQueue<UnitAction> pendingActionQueue = new PendingActionQueue<>();
	private UnitAction currentAction;

	private Consumer<Unit> onPendingActionQueueEmpty;

	private Rng rng;

	@Getter
	private Party party;

	@Getter
	@Setter
	private Pet activePet;

	private SimulationContext simulationContext;

	protected UnitImpl(String name, Phase phase, CharacterClass characterClass, int level, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo) {
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo);
		this.resources.setHealth(10_000, 10_000);
		this.resources.setMana(10_000, 10_000);
		new Raid().getFirstParty().add(this);
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

	@Override
	public void ensureAction() {
		if (getSimulation().isFinished()) {
			return;
		}

		if (hasActionInProgress() || isOnCooldown(GCD)) {
			return;
		}

		if (pendingActionQueue.isEmpty()) {
			onPendingActionQueueEmpty.accept(this);

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
	public void setOnPendingActionQueueEmpty(Consumer<Unit> onPendingActionQueueEmpty) {
		this.onPendingActionQueueEmpty = onPendingActionQueueEmpty;
	}

	public void whenNoActionIdleForever() {
		setOnPendingActionQueueEmpty(x -> x.idleUntil(TIME_IN_INFINITY));
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
		var primaryTarget = getPrimaryTarget(ability, target);
		cast(ability, primaryTarget);
	}

	private void cast(Ability ability, PrimaryTarget primaryTarget) {
		var action = new CastSpellAction(this, ability, primaryTarget);
		enqueueAction(action);
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
	public boolean canCast(AbilityId abilityId) {
		return canCast(abilityId, null);
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
				canPaySpellCost(ability) &&
				!isSchoolPrevented(ability.getSchool());
	}

	private boolean hasAllValidTargets(Ability ability, TargetResolver targetResolver) {
		if (targetResolver == null) {
			return false;
		}
		var targets = ability.getTargets();
		return targetResolver.hasAllValidTargets(targets);
	}

	private boolean canPaySpellCost(Ability ability) {
		if (ability instanceof ActivatedAbility) {
			return true;
		}
		var costSnapshot = getSpellCostSnapshot(ability);
		return getResources().canPay(costSnapshot.getCostToPay());
	}

	private boolean isSchoolPrevented(SpellSchool school) {
		return getBuffs().getStream().anyMatch(x -> x.isSchoolPrevented(school)) ||
				effects.getStream().anyMatch(x -> x.isSchoolPrevented(school));
	}

	@Override
	public SpellCostSnapshot paySpellCost(Ability ability) {
		var costSnapshot = getSpellCostSnapshot(ability);
		var cost = costSnapshot.getCostToPay();

		paySpellCost(ability, cost);

		return costSnapshot;
	}

	protected void paySpellCost(Ability ability, Cost cost) {
		getResources().pay(cost, ability);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(AbilityId abilityId) {
		var ability = getAbility(abilityId).orElseThrow();
		return getSpellCastSnapshot(ability);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Ability ability) {
		return getCharacterCalculationService().getSpellCastSnapshot(this, ability, (Character) null);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(AbilityId abilityId) {
		var ability = getAbility(abilityId).orElseThrow();
		return getSpellCostSnapshot(ability);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Ability ability) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getSpellCostSnapshot(this, ability, null, baseStats);
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
	public EffectDurationSnapshot getEffectDurationSnapshot(AbilityId abilityId, Unit target, ApplyEffect command) {
		var ability = getAbility(abilityId).orElseThrow();
		return getEffectDurationSnapshot(ability, target, command);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Spell spell, Unit target, ApplyEffect command) {
		return getCharacterCalculationService().getEffectDurationSnapshot(this, spell, target, command);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Spell spell, Unit target, DealDamageDirectly command) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getDirectSpellDamageSnapshot(this, spell, target, command, baseStats);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectHealingSnapshot(Spell spell, Unit target, HealDirectly command) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getDirectHealingSnapshot(this, spell, target, command, baseStats);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Spell spell, Unit target, DealDamagePeriodically command) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getPeriodicSpellDamageSnapshot(this, spell, target, command, baseStats);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Spell spell, Unit target, HealPeriodically command) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getPeriodicHealingSnapshot(this, spell, target, command, baseStats);
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
	public BaseStatsSnapshot getBaseStatsSnapshot() {
		return getCharacterCalculationService().getBaseStatsSnapshot(this);
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
	public int increaseHealth(int amount, boolean crit, Spell spell, Unit caster) {
		return getResources().increaseHealth(amount, crit, spell, caster);
	}

	@Override
	public int decreaseHealth(int amount, boolean crit, Spell spell, Unit caster) {
		return getResources().decreaseHealth(amount, crit, spell, caster);
	}

	@Override
	public int increaseMana(int amount, boolean crit, Spell spell, Unit caster) {
		return getResources().increaseMana(amount, crit, spell, caster);
	}

	@Override
	public int decreaseMana(int amount, boolean crit, Spell spell, Unit caster) {
		return getResources().decreaseMana(amount, crit, spell, caster);
	}

	@Override
	public void addEffect(EffectInstance effect, EffectReplacementMode replacementMode) {
		effects.addEffect(effect, replacementMode);
	}

	@Override
	public void addHiddenEffect(String effectName, int numStacks) {
		addHiddenEffect(effectName, numStacks, Duration.INFINITE);
	}

	@Override
	public void addHiddenEffect(String effectName, int numStacks, AnyDuration duration) {
		var effect = getSpellRepository().getEffect(effectName, getPhaseId()).orElseThrow();
		var effectInstance = new NonPeriodicEffectInstance(
				this,
				this,
				effect,
				duration,
				numStacks,
				1,
				null,
				null,
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
	public boolean isUnderEffect(AbilityId abilityId, Unit owner) {
		return effects.isUnderEffect(abilityId, owner);
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
	public boolean hasEffect(AbilityId requiredEffect, Unit effectOwner) {
		return effects.isUnderEffect(requiredEffect, effectOwner);
	}

	@Override
	public boolean hasEffect(AbilityId abilityId) {
		return effects.isUnderEffect(abilityId);
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

	// character interface

	@Override
	public void setTarget(Character target) {
		if (target != null && !(target instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
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
	public void onResourcesNeedRefresh() {
		resourcesNeedRefresh = true;
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

	public void setParty(Party party) {
		if (this.party != null) {
			this.party.remove(this);
		}
		this.party = party;
	}

	@Override
	public void setActivePet(PetType petType) {
		if (petType != null) {
			this.activePet = getGameVersion().getPet(petType).orElseThrow();
		} else {
			this.activePet = null;
		}
	}
}
