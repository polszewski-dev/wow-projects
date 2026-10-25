package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedDurationStats;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class SummonCalculations extends Calculations {
	public static AnyDuration getSummonDuration(Character caster, Spell spell, AnyDuration baseDuration) {
		if (baseDuration.isInfinite()) {
			return baseDuration;
		}

		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null);
		var durationStats = new AccumulatedDurationStats(conditionArgs);

		accumulateEffects(caster, durationStats);

		var baseDurationSeconds = ((Duration) baseDuration).getSeconds();
		var duration = durationStats.getDuration();
		var durationPct = durationStats.getDurationPct();

		var result = addAndMultiplyByPct(baseDurationSeconds, duration, durationPct);

		return Duration.seconds(result);
	}
}
