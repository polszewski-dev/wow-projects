package wow.simulator.model.unit.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.snapshot.*;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.talent.TalentTree;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.cooldown.Cooldowns;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.Effects;
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

import static wow.commons.model.spell.GcdCooldownId.GCD;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public abstract class UnitImpl extends CharacterImpl implements Unit, SimulationContextAware {
	private static final IdGenerator<UnitId> ID_GENERATOR = new IdGenerator<>(UnitId::new);

	protected final UnitId id = ID_GENERATOR.newId();

	protected final UnitResources resources = new UnitResources(this);
	protected final Effects effects = new Effects(this);
	private final Cooldowns cooldowns = new Cooldowns(this);

	private final PendingActionQueue<UnitAction> pendingActionQueue = new PendingActionQueue<>();
	private UnitAction currentAction;

	private Consumer<Unit> onPendingActionQueueEmpty;

	private Rng rng;

	@Getter
	@Setter
	private Party party;

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
		if (hasActionInProgress() || isOnCooldown(GCD)) {
			return;
		}

		if (pendingActionQueue.isEmpty()) {
			onPendingActionQueueEmpty.accept(this);
		}

		if (pendingActionQueue.isEmpty()) {
			throw new IllegalStateException();
		}

		var newAction = pendingActionQueue.removeEarliestAction();

		startAction(newAction);
	}

	private void startAction(UnitAction newAction) {
		this.currentAction = newAction;
		getScheduler().add(newAction);
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

	@Override
	public void cast(String abilityName) {
		var abilityId = AbilityId.parse(abilityName);
		cast(abilityId);
	}

	@Override
	public void cast(AbilityId abilityId) {
		var ability = getAbility(abilityId).orElseThrow();
		var primaryTarget = getPrimaryTarget(ability, null);
		cast(ability, primaryTarget);
	}

	@Override
	public void cast(String abilityName, Unit target) {
		var abilityId = AbilityId.parse(abilityName);
		cast(abilityId, target);
	}

	@Override
	public void cast(AbilityId abilityId, Unit target) {
		var ability = getAbility(abilityId).orElseThrow();
		var primaryTarget = getPrimaryTarget(ability, target);
		cast(ability, primaryTarget);
	}

	private void cast(Ability ability, PrimaryTarget primaryTarget) {
		var action = new CastSpellAction(this, ability, primaryTarget);
		pendingActionQueue.add(action);
	}

	@Override
	public PrimaryTarget getPrimaryTarget(Ability ability, Unit explicitTarget) {
		var resolver = new PrimaryTargetResolver(ability, this, getTarget(), explicitTarget);
		return resolver.getPrimaryTarget();
	}

	@Override
	public void idleUntil(AnyTime time) {
		pendingActionQueue.add(new IdleAction(this, time));
	}

	@Override
	public void idleFor(Duration duration) {
		idleUntil(getClock().now().add(duration));
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
		var abilityId = AbilityId.parse(abilityName);
		return canCast(abilityId);
	}

	@Override
	public boolean canCast(AbilityId abilityId) {
		var ability = getAbility(abilityId).orElseThrow();
		var primaryTarget = getPrimaryTarget(ability, null);
		return canCast(ability, primaryTarget);
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
				hasRequiredEffect(ability, primaryTarget) &&
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
		return resources.canPay(costSnapshot.getCostToPay());
	}

	private boolean hasRequiredEffect(Ability ability, PrimaryTarget primaryTarget) {
		if (ability.getRequiredEffect() == null) {
			return true;
		}
		var target = primaryTarget.requireSingleTarget();
		return target.isUnderEffect(ability.getRequiredEffect(), this);
	}

	private boolean isSchoolPrevented(SpellSchool school) {
		return getBuffs().getStream().anyMatch(x -> x.isSchoolPrevented(school)) ||
				effects.getStream().anyMatch(x -> x.isSchoolPrevented(school));
	}

	@Override
	public SpellCostSnapshot paySpellCost(Ability ability) {
		var costSnapshot = getSpellCostSnapshot(ability);
		var cost = costSnapshot.getCostToPay();

		resources.pay(cost, ability);

		return costSnapshot;
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
		return getEffectDurationSnapshot(ability, target);
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Spell spell, Unit target) {
		return getCharacterCalculationService().getEffectDurationSnapshot(this, spell, target);
	}

	@Override
	public DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Spell spell, Unit target, DirectComponent directComponent) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getDirectSpellDamageSnapshot(this, spell, target, directComponent, baseStats);
	}

	@Override
	public PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Spell spell, Unit target) {
		var baseStats = getBaseStatsSnapshot();
		return getCharacterCalculationService().getPeriodicSpellDamageSnapshot(this, spell, target, baseStats);
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
		return resources.getCurrentHealth();
	}

	@Override
	public int getCurrentMana() {
		return resources.getCurrentMana();
	}

	@Override
	public int increaseHealth(int amount, boolean crit, Spell spell) {
		return resources.increaseHealth(amount, crit, spell);
	}

	@Override
	public int decreaseHealth(int amount, boolean crit, Spell spell) {
		return resources.decreaseHealth(amount, crit, spell);
	}

	@Override
	public int increaseMana(int amount, boolean crit, Spell spell) {
		return resources.increaseMana(amount, crit, spell);
	}

	@Override
	public int decreaseMana(int amount, boolean crit, Spell spell) {
		return resources.decreaseMana(amount, crit, spell);
	}

	@Override
	public void addEffect(EffectInstance effect, EffectReplacementMode replacementMode) {
		effects.addEffect(effect, replacementMode);
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
	public boolean hasEffect(AbilityId requiredEffect, Unit effectOwner) {
		return effects.isUnderEffect(requiredEffect, effectOwner);
	}

	@Override
	public boolean hasEffect(AbilityId abilityId) {
		return effects.isUnderEffect(abilityId);
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
	public Percent getHealthPct() {
		return Percent.of(100.0 * resources.getCurrentHealth() / resources.geMaxHealth());
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
}
