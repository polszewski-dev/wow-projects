package wow.simulator.util;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.ResourceType;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.TALENT;

/**
 * User: POlszewski
 * Date: 2025-01-30
 */
public sealed interface TestEvent {
	Time time();

	default boolean nameContains(String name) {
		return getClass().getSimpleName().toLowerCase().contains(name.toLowerCase());
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
		return this instanceof HasEffectType e && e.type() == TALENT;
	}

	default boolean isEffect() {
		return this instanceof HasEffectType;
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

	default boolean isBeginCast(String spellName) {
		return this instanceof BeginCast bc && bc.spell().equals(spellName);
	}

	default boolean isEndCast() {
		return this instanceof EndCast;
	}

	default boolean isCast() {
		return isBeginCast() || isEndCast();
	}

	record BeginGcd(Time time, Unit caster) implements TestEvent {}

	record EndGcd(Time time, Unit caster) implements TestEvent {}

	record BeginCast(Time time, Unit caster, String spell, Duration castTime) implements TestEvent {}

	record EndCast(Time time, Unit caster, String spell) implements TestEvent {}

	record BeginChannel(Time time, Unit caster, String spell, Duration channelTime) implements TestEvent {}

	record EndChannel(Time time, Unit caster, String spell) implements TestEvent {}

	record CanNotBeCasted(Time time, Unit caster, String spell) implements TestEvent {}

	record CastInterrupted(Time time, Unit caster, String spell) implements TestEvent {}

	record ChannelInterrupted(Time time, Unit caster, String spell) implements TestEvent {}

	record SpellResisted(Time time, Unit caster, String spell, Unit target) implements TestEvent {}

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

	interface HasEffectType {
		EffectType type();
	}

	record EffectApplied(Time time, String name, EffectType type, Unit target, AnyDuration duration) implements TestEvent, HasEffectType {}

	record EffectStacked(Time time, String name, EffectType type, Unit target, int numStacks) implements TestEvent, HasEffectType {}

	record EffectStacksIncreased(Time time, String name, EffectType type, Unit target, int numStacks) implements TestEvent, HasEffectType {}

	record EffectStacksDecreased(Time time, String name, EffectType type, Unit target, int numStacks) implements TestEvent, HasEffectType {}

	record EffectChargesIncreased(Time time, String name, EffectType type, Unit target, int numCharges) implements TestEvent, HasEffectType {}

	record EffectChargesDecreased(Time time, String name, EffectType type, Unit target, int numCharges) implements TestEvent, HasEffectType {}

	record EffectExpired(Time time, String name, EffectType type, Unit target) implements TestEvent, HasEffectType {}

	record EffectRemoved(Time time, String name, EffectType type, Unit target) implements TestEvent, HasEffectType {}

	record CooldownStarted(Time time, Unit caster, CooldownId cooldownId, Duration duration) implements TestEvent {}

	record CooldownExpired(Time time, Unit caster, CooldownId cooldownId) implements TestEvent {}
}
