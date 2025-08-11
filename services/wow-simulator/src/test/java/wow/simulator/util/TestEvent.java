package wow.simulator.util;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-30
 */
public sealed interface TestEvent {
	Time time();

	default boolean nameContains(String cooldown) {
		return getClass().getSimpleName().toLowerCase().contains(cooldown.toLowerCase());
	}

	default boolean isDamage() {
		return this instanceof DecreasedResource dr && dr.type == HEALTH;
	}

	default boolean isHealing() {
		return this instanceof IncreasedResource ir && ir.type == HEALTH;
	}

	default boolean isManaPaid() {
		return this instanceof DecreasedResource dr && dr.type == MANA;
	}

	default boolean isManaGained() {
		return this instanceof IncreasedResource ir && ir.type == MANA;
	}

	default boolean isTalentEffect() {
		return nameContains("TalentEffect");
	}

	default boolean isEffect() {
		return nameContains("Effect");
	}

	default boolean isCooldown() {
		return nameContains("Cooldown");
	}

	default boolean isSpellResisted() {
		return this instanceof SpellResisted;
	}

	default boolean isEffectApplied() {
		return this instanceof EffectApplied;
	}

	default boolean isBeginCast() {
		return this instanceof BeginCast;
	}

	record BeginGcd(Time time, Unit caster) implements TestEvent {}

	record EndGcd(Time time, Unit caster) implements TestEvent {}

	record BeginCast(Time time, Unit caster, AbilityId spell, Duration castTime) implements TestEvent {}

	record EndCast(Time time, Unit caster, AbilityId spell) implements TestEvent {}

	record BeginChannel(Time time, Unit caster, AbilityId spell, Duration channelTime) implements TestEvent {}

	record EndChannel(Time time, Unit caster, AbilityId spell) implements TestEvent {}

	record CanNotBeCasted(Time time, Unit caster, AbilityId spell) implements TestEvent {}

	record CastInterrupted(Time time, Unit caster, AbilityId spell) implements TestEvent {}

	record ChannelInterrupted(Time time, Unit caster, AbilityId spell) implements TestEvent {}

	record SpellResisted(Time time, Unit caster, AbilityId spell, Unit target) implements TestEvent {}

	record IncreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, String spell) implements TestEvent {
		public boolean isHealing(String spell, Unit target) {
			return type == HEALTH && this.spell.equals(spell) && this.target == target;
		}

		public boolean isManaGain(String spell, Unit target) {
			return type == MANA && this.spell.equals(spell) && this.target == target;
		}
	}

	record DecreasedResource(Time time, int amount, ResourceType type, boolean crit, Unit target, String spell) implements TestEvent {
		public boolean isDamage(String spell, Unit target) {
			return type == HEALTH && this.spell.equals(spell) && this.target == target;
		}

		public boolean isManaPaid(String spell, Unit target) {
			return type == MANA && this.spell.equals(spell) && this.target == target;
		}
	}

	record EffectApplied(Time time, AbilityId spell, Unit target, AnyDuration duration) implements TestEvent {}

	record TalentEffectApplied(Time time, TalentId talentId, Unit target, AnyDuration duration) implements TestEvent {}

	record ItemEffectApplied(Time time, String itemName, Unit target, AnyDuration duration) implements TestEvent {}

	record EffectStacked(Time time, AbilityId spell, Unit target, int numStacks) implements TestEvent {}

	record TalentEffectStacked(Time time, TalentId talentId, Unit target, int numStacks) implements TestEvent {}

	record ItemEffectStacked(Time time, String itemName, Unit target, int numStacks) implements TestEvent {}

	record EffectStacksIncreased(Time time, AbilityId spell, Unit target, int numStacks) implements TestEvent {}

	record TalentEffectStacksIncreased(Time time, TalentId talentId, Unit target, int numStacks) implements TestEvent {}

	record ItemEffectStacksIncreased(Time time, String itemName, Unit target, int numStacks) implements TestEvent {}

	record EffectStacksDecreased(Time time, AbilityId spell, Unit target, int numStacks) implements TestEvent {}

	record TalentEffectStacksDecreased(Time time, TalentId talentId, Unit target, int numStacks) implements TestEvent {}

	record ItemEffectStacksDecreased(Time time, String itemName, Unit target, int numStacks) implements TestEvent {}

	record EffectChargesIncreased(Time time, AbilityId spell, Unit target, int numCharges) implements TestEvent {}

	record TalentEffectChargesIncreased(Time time, TalentId talentId, Unit target, int numCharges) implements TestEvent {}

	record ItemEffectChargesIncreased(Time time, String itemName, Unit target, int numCharges) implements TestEvent {}

	record EffectChargesDecreased(Time time, AbilityId spell, Unit target, int numCharges) implements TestEvent {}

	record TalentEffectChargesDecreased(Time time, TalentId talentId, Unit target, int numCharges) implements TestEvent {}

	record ItemEffectChargesDecreased(Time time, String itemName, Unit target, int numCharges) implements TestEvent {}

	record EffectExpired(Time time, AbilityId spell, Unit target) implements TestEvent {}

	record TalentEffectExpired(Time time, TalentId talentId, Unit target) implements TestEvent {}

	record ItemEffectExpired(Time time, String itemName, Unit target) implements TestEvent {}

	record EffectRemoved(Time time, AbilityId spell, Unit target) implements TestEvent {}

	record TalentEffectRemoved(Time time, TalentId talentId, Unit target) implements TestEvent {}

	record ItemEffectRemoved(Time time, String itemName, Unit target) implements TestEvent {}

	record CooldownStarted(Time time, Unit caster, CooldownId cooldownId, Duration duration) implements TestEvent {}

	record CooldownExpired(Time time, Unit caster, CooldownId cooldownId) implements TestEvent {}
}
