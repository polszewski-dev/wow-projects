package wow.commons.model.spells;

import wow.commons.model.Duration;
import wow.commons.model.effects.EffectId;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
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

	public SpellRankInfo(SpellId spellId, int rank, int level, int manaCost, Duration castTime, boolean channeled, int minDmg, int maxDmg, int minDmg2, int maxDmg2, int dotDmg, int numTicks, Duration tickInterval, AdditionalCost additionalCost, EffectId appliedEffect, Duration appliedEffectDuration) {
		this.spellId = spellId;
		this.rank = rank;
		this.level = level;
		this.manaCost = manaCost;
		this.castTime = castTime;
		this.channeled = channeled;
		this.minDmg = minDmg;
		this.maxDmg = maxDmg;
		this.minDmg2 = minDmg2;
		this.maxDmg2 = maxDmg2;
		this.dotDmg = dotDmg;
		this.numTicks = numTicks;
		this.tickInterval = tickInterval;
		this.additionalCost = additionalCost;
		this.appliedEffect = appliedEffect;
		this.appliedEffectDuration = appliedEffectDuration;
	}

	public static SpellRankInfo dummy(SpellId spellId) {
		return new SpellRankInfo(spellId, 0, 1, 0, Duration.ZERO, false, 0, 0, 0, 0, 0, 0, null, null, null, null);
	}

	public SpellId getSpellId() {
		return spellId;
	}

	public int getRank() {
		return rank;
	}

	public int getLevel() {
		return level;
	}

	public int getManaCost() {
		return manaCost;
	}

	public Duration getCastTime() {
		return castTime;
	}

	public boolean isChanneled() {
		return channeled;
	}

	public int getMinDmg() {
		return minDmg;
	}

	public int getMaxDmg() {
		return maxDmg;
	}

	public int getMinDmg2() {
		return minDmg2;
	}

	public int getMaxDmg2() {
		return maxDmg2;
	}

	public int getDotDmg() {
		return dotDmg;
	}

	public int getNumTicks() {
		return numTicks;
	}

	public Duration getTickInterval() {
		if (channeled) {
			return castTime.divideBy(numTicks);
		}
		return tickInterval;
	}

	public AdditionalCost getAdditionalCost() {
		return additionalCost;
	}

	public EffectId getAppliedEffect() {
		return appliedEffect;
	}

	public Duration getAppliedEffectDuration() {
		return appliedEffectDuration;
	}

	public Duration getDoTDuration() {
		return getTickInterval().multiplyBy(numTicks);
	}
}
