package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedDurationStats;
import wow.character.model.snapshot.AccumulatedReceivedEffectStats;
import wow.character.model.snapshot.EffectDurationSnapshot;
import wow.character.util.AbstractEffectCollector;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Spell;

import java.util.ArrayList;
import java.util.List;

import static wow.character.model.snapshot.AttributePredicates.ANY_TARGET;
import static wow.character.service.impl.calc.CastCalculations.getActualCastTime;
import static wow.character.service.impl.calc.CastCalculations.getHastePct;
import static wow.commons.model.attribute.AttributeId.EFFECT_PCT;
import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class EffectCalculations extends Calculations {
	public static AccumulatedDurationStats newAccumulatedDurationStats(Character caster, Spell spell, Character target, ApplyEffect command) {
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, target);

		conditionArgs.setAppliedEffect(command.effect());

		return new AccumulatedDurationStats(conditionArgs);
	}

	private static AccumulatedDurationStats getAccumulatedDurationStats(Character caster, Spell spell, Character target, ApplyEffect command) {
		var durationStats = newAccumulatedDurationStats(caster, spell, target, command);

		accumulateEffects(caster, durationStats);
		return durationStats;
	}

	public static AccumulatedReceivedEffectStats newAccumulatedReceivedEffectStats(Character target, Spell spell, ApplyEffect command) {
		var conditionArgs = AttributeConditionArgs.forSpellTarget(target, spell);

		conditionArgs.setAppliedEffect(command.effect());

		return new AccumulatedReceivedEffectStats(conditionArgs);
	}

	private static AccumulatedReceivedEffectStats getAccumulatedReceivedEffectStats(Spell spell, Character target, ApplyEffect command) {
		if (spell instanceof Ability ability && (ability.isChanneled() || command.target().hasType(GROUND))) {
			return null;
		}

		var receivedEffectStats = newAccumulatedReceivedEffectStats(target, spell, command);

		accumulateEffects(target, receivedEffectStats);
		return receivedEffectStats;
	}

	public static EffectDurationSnapshot getEffectDurationSnapshot(Character caster, Spell spell, Character target, ApplyEffect command) {
		var durationStats = getAccumulatedDurationStats(caster, spell, target, command);
		var receivedEffectStats = getAccumulatedReceivedEffectStats(spell, target, command);

		return getEffectDurationSnapshot(caster, spell, command, durationStats, receivedEffectStats);
	}

	public static EffectDurationSnapshot getEffectDurationSnapshot(Character caster, Spell spell, ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		if (spell instanceof Ability ability && ability.isChanneled()) {
			return getChanneledEffectDurationSnapshot(caster, command, durationStats, receivedEffectStats);
		}

		var tickInterval = command.effect().getTickInterval();

		if (tickInterval != null) {
			return getPeriodicEffectDurationSnapshot(command, durationStats, receivedEffectStats);
		} else {
			return getNonPeriodicEffectDurationSnapshot(command, durationStats, receivedEffectStats);
		}
	}

	private static EffectDurationSnapshot getChanneledEffectDurationSnapshot(Character caster, ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var effect = command.effect();

		var baseDuration = (Duration) command.duration();
		var baseTickIntervalMillis = effect.getTickInterval().millis();
		var duration = getEffectDuration(baseDuration, durationStats, receivedEffectStats);
		var durationMillis = duration.millis();
		var numTicks = durationMillis / baseTickIntervalMillis;
		var numTicksNotRounded = (double) durationMillis / baseTickIntervalMillis;

		var hastePct = getHastePct(caster, durationStats);
		var tickIntervalMillis = (long) getActualCastTime(baseTickIntervalMillis, hastePct);
		var channelTimeMillis = (long) (tickIntervalMillis * numTicksNotRounded);

		var durationSnapshot = new EffectDurationSnapshot();

		durationSnapshot.setNumTicks((int) numTicks);
		durationSnapshot.setTickInterval(Duration.millis(tickIntervalMillis));
		durationSnapshot.setDuration(Duration.millis(channelTimeMillis));

		return durationSnapshot;
	}

	private static EffectDurationSnapshot getPeriodicEffectDurationSnapshot(ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var effect = command.effect();
		var baseDuration = (Duration) command.duration();
		var tickInterval = effect.getTickInterval();
		var duration = getEffectDuration(baseDuration, durationStats, receivedEffectStats);
		var numTicks = duration.millis() / tickInterval.millis();

		var durationSnapshot = new EffectDurationSnapshot();

		durationSnapshot.setNumTicks((int) numTicks);
		durationSnapshot.setTickInterval(tickInterval);
		durationSnapshot.setDuration(duration);

		return durationSnapshot;
	}

	private static EffectDurationSnapshot getNonPeriodicEffectDurationSnapshot(ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var baseDuration = command.duration();
		var duration = baseDuration.isInfinite()
				? baseDuration
				: getEffectDuration((Duration) baseDuration, durationStats, receivedEffectStats);

		var durationSnapshot = new EffectDurationSnapshot();

		durationSnapshot.setNumTicks(0);
		durationSnapshot.setTickInterval(Duration.ZERO);
		durationSnapshot.setDuration(duration);

		return durationSnapshot;
	}

	private static Duration getEffectDuration(Duration baseDuration, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var baseDurationSeconds = baseDuration.getSeconds();
		var duration = durationStats.getDuration();
		var durationPct = durationStats.getDurationPct();

		if (receivedEffectStats != null) {
			duration += receivedEffectStats.getReceivedEffectDuration();
			durationPct += receivedEffectStats.getReceivedEffectDurationPct();
		}

		var result = addAndMultiplyByPct(baseDurationSeconds, duration, durationPct);

		return Duration.seconds(result);
	}

	public static EffectAugmentations getEffectAugmentations(Character caster, Spell spell, Character target) {
		if (!(spell instanceof Ability ability)) {
			return EffectAugmentations.EMPTY;
		}

		var collector = new EffectAugmentationCollector(caster, ability, target);

		collector.solveAll();

		return new EffectAugmentations(
				collector.accumulatedEffectIncreasePct.totalValue,
				collector.extraModifiers,
				collector.extraStatConversions,
				collector.extraEvents
		);
	}

	private static class EffectAugmentationCollector extends AbstractEffectCollector.OnlyEffects {
		final Ability ability;
		final Character target;
		final AccumulatedSingleStat accumulatedEffectIncreasePct;

		List<Attribute> extraModifiers = new ArrayList<>();
		List<StatConversion> extraStatConversions = new ArrayList<>();
		List<Event> extraEvents = new ArrayList<>();

		EffectAugmentationCollector(Character caster, Ability ability, Character target) {
			super(caster);
			this.ability = ability;
			this.target = target;
			var args = AttributeConditionArgs.forSpell(caster, ability, null);
			this.accumulatedEffectIncreasePct = new AccumulatedSingleStat(EFFECT_PCT, args);
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			if (!effect.hasAugmentedAbilities()) {
				checkForMatchingEffectIncreases(effect, stackCount);
			} else if (effect.augments(ability.getAbilityId())) {
				extractAugmentations(effect);
			}
		}

		private void extractAugmentations(Effect effect) {
			var modifierAttributeList = effect.getModifierAttributeList();

			extraModifiers.addAll(modifierAttributeList);
			extraStatConversions.addAll(effect.getStatConversions());
			extraEvents.addAll(effect.getEvents());
		}

		private void checkForMatchingEffectIncreases(Effect effect, int stackCount) {
			var modifierAttributeList = effect.getModifierAttributeList();

			accumulatedEffectIncreasePct.accumulateAttributes(modifierAttributeList, stackCount, ANY_TARGET);
		}
	}
}
