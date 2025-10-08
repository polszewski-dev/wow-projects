package wow.simulator.model.unit;

import wow.character.model.character.Character;
import wow.character.model.effect.EffectCollector;
import wow.character.model.snapshot.*;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.DirectComponent;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.AnyTime;
import wow.simulator.simulation.SimulationContextSource;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Unit extends Character, SimulationContextSource {
	UnitId getId();

	String getName();

	@Override
	Unit getTarget();

	void setTarget(Unit target);

	StatSummary getStats();

	void setOnPendingActionQueueEmpty(Consumer<Unit> onPendingActionQueueEmpty);

	void ensureAction();

	void cast(AbilityId abilityId);

	void cast(AbilityId abilityId, Unit target);

	void cast(String abilityName);

	void cast(String abilityName, Unit target);

	PrimaryTarget getPrimaryTarget(Ability ability, Unit explicitTarget);

	void idleUntil(AnyTime time);

	void idleFor(Duration duration);

	void triggerGcd(Duration duration);

	void interruptCurrentAction();

	boolean canCast(String abilityName);

	boolean canCast(String abilityName, Unit target);

	boolean canCast(AbilityId abilityId);

	boolean canCast(AbilityId abilityId, Unit target);

	boolean canCast(Ability ability, Unit target);

	boolean canCast(Ability ability, PrimaryTarget primaryTarget);

	SpellCostSnapshot paySpellCost(Ability ability);

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

	Percent getManaPct();

	int increaseHealth(int amount, boolean crit, Spell spell);

	int decreaseHealth(int amount, boolean crit, Spell spell);

	int increaseMana(int amount, boolean crit, Spell spell);

	int decreaseMana(int amount, boolean crit, Spell spell);

	void addEffect(EffectInstance effect, EffectReplacementMode replacementMode);

	void removeEffect(EffectInstance effect);

	void removeEffect(AbilityId abilityId, Unit owner);

	boolean isUnderEffect(AbilityId abilityId, Unit owner);

	Optional<EffectInstance> getEffect(AbilityId abilityId, Unit owner);

	boolean hasEffect(AbilityId requiredEffect, Unit effectOwner);

	boolean hasEffect(Pattern effectNamePattern);

	boolean hasEffect(Pattern effectNamePattern, Unit effectOwner);

	boolean isOnCooldown(AbilityId abilityId);

	boolean isOnCooldown(Ability ability);

	boolean isOnCooldown(CooldownId cooldownId);

	Duration getRemainingCooldown(AbilityId abilityId);

	void triggerCooldown(Ability ability, Duration actualDuration);

	void triggerCooldown(CooldownId cooldownId, Duration actualDuration);

	Party getParty();

	default Raid getRaid() {
		return getParty().getRaid();
	}

	void setParty(Party party);

	void collectAuras(EffectCollector collector);

	static boolean areFriendly(Unit first, Unit second) {
		return first.getClass() == second.getClass();
	}

	static boolean areHostile(Unit first, Unit second) {
		return !areFriendly(first, second);
	}
}
