package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.effects.EffectId;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@AllArgsConstructor
@Getter
public class SpellRankInfo {
	private final SpellId spellId;
	private final int rank;
	private final int level;
	private final int manaCost;
	private final Duration castTime;
	private final boolean channeled;
	private final int minDmg;
	private final int maxDmg;
	private final int minDmg2;
	private final int maxDmg2;
	private final int dotDmg;
	private final int numTicks;
	private final Duration tickInterval;
	private final AdditionalCost additionalCost;
	private final EffectId appliedEffect;
	private final Duration appliedEffectDuration;

	public Duration getTickInterval() {
		if (channeled) {
			return castTime.divideBy(numTicks);
		}
		return tickInterval;
	}

	public Duration getDoTDuration() {
		return getTickInterval().multiplyBy(numTicks);
	}
}
