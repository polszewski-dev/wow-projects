package wow.simulator.model.unit;

import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.context.SpellCastContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.model.update.StageUpdateable;
import wow.simulator.simulation.SimulationContextSource;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Unit extends Character, StageUpdateable, SimulationContextSource {
	UnitId getId();

	String getName();

	@Override
	Unit getTarget();

	void setTarget(Unit target);

	StatSummary getStats();

	void setOnPendingActionQueueEmpty(Consumer<Unit> onPendingActionQueueEmpty);

	void cast(AbilityId abilityId);

	void cast(AbilityId abilityId, Unit target);

	void idleUntil(Time time);

	void idleFor(Duration duration);

	void triggerGcd(Duration gcd, UnitAction sourceAction);

	void interruptCurrentAction();

	boolean canCast(AbilityId abilityId);

	boolean canCast(AbilityId abilityId, Unit target);

	boolean canCast(Ability ability, Unit target);

	boolean canCast(Ability ability, TargetResolver targetResolver);

	TargetResolver getTargetResolver(Unit target);

	SpellCostSnapshot paySpellCost(Ability ability);

	SpellCastContext getSpellCastContext(Ability ability);

	SpellCastSnapshot getSpellCastSnapshot(AbilityId abilityId);

	SpellCastSnapshot getSpellCastSnapshot(Ability ability);

	SpellCostSnapshot getSpellCostSnapshot(AbilityId abilityId);

	SpellCostSnapshot getSpellCostSnapshot(Ability ability);

	double getSpellHitPct(Spell spell, Unit target);

	EffectDurationSnapshot getEffectDurationSnapshot(AbilityId abilityId, Unit target);

	EffectDurationSnapshot getEffectDurationSnapshot(Spell spell, Unit target);

	DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Spell spell, Unit target, DirectComponent directComponent);

	PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Spell spell, Unit target);

	BaseStatsSnapshot getBaseStatsSnapshot();

	Rng getRng();

	int getCurrentHealth();

	int getCurrentMana();

	int increaseHealth(int amount, boolean crit, Spell spell);

	int decreaseHealth(int amount, boolean crit, Spell spell);

	int increaseMana(int amount, boolean crit, Spell spell);

	int decreaseMana(int amount, boolean crit, Ability ability);

	void addEffect(EffectInstance effect);

	void removeEffect(EffectInstance effect);

	boolean isUnderEffect(AbilityId abilityId, Unit owner);

	Optional<EffectInstance> getEffect(AbilityId abilityId, Unit owner);

	boolean hasEffect(AbilityId requiredEffect, Unit effectOwner);

	boolean isOnCooldown(AbilityId abilityId);

	boolean isOnCooldown(Ability ability);

	void triggerCooldown(Ability ability, Duration actualDuration);

	static boolean areFriendly(Unit first, Unit second) {
		return first.getClass() == second.getClass();
	}

	static boolean areHostile(Unit first, Unit second) {
		return !areFriendly(first, second);
	}
}
