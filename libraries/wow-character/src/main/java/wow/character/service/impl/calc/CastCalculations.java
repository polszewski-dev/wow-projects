package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedCastStats;
import wow.character.model.snapshot.AccumulatedDurationStats;
import wow.character.model.snapshot.SpellCastSnapshot;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.spell.Ability;

import static java.lang.Math.max;
import static wow.commons.constant.SpellConstants.GCD;
import static wow.commons.constant.SpellConstants.MIN_GCD;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class CastCalculations extends Calculations {
	public static AccumulatedCastStats newAccumulatedCastStats(Character caster, Ability ability, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpell(caster, ability, target);

		return new AccumulatedCastStats(conditionArgs);
	}

	private static AccumulatedCastStats getAccumulatedCastStats(Character caster, Ability ability, Character target) {
		var castStats = newAccumulatedCastStats(caster, ability, target);

		accumulateEffects(caster, castStats);
		return castStats;
	}

	public static SpellCastSnapshot getSpellCastSnapshot(Character caster, Ability ability, Character target) {
		var castStats = getAccumulatedCastStats(caster, ability, target);

		return getSpellCastSnapshot(caster, ability, castStats);
	}

	public static SpellCastSnapshot getSpellCastSnapshot(Character caster, Ability ability, AccumulatedCastStats castStats) {
		var snapshot = new SpellCastSnapshot();

		var hastePct = getHastePct(caster, castStats);
		var castTime = getCastTime(ability, castStats, hastePct);
		var gcd = getGcd(hastePct);

		snapshot.setHastePct(hastePct);
		snapshot.setCastTime(castTime);
		snapshot.setGcd(gcd);

		return snapshot;
	}

	private static double getCastTime(Ability ability, AccumulatedCastStats castStats, double hastePct) {
		var modifiedCastTime = addAndMultiplyByPct(
				ability.getCastTime().getSeconds(),
				castStats.getCastTime(),
				castStats.getCastTimePct()
		);

		return getActualCastTime(modifiedCastTime, hastePct);
	}

	private static double getGcd(double hastePct) {
		var gcd = GCD.getSeconds();
		var minGcd = MIN_GCD.getSeconds();

		return max(getActualCastTime(gcd, hastePct), minGcd);
	}

	static double getActualCastTime(double baseCastTime, double hastePct) {
		return baseCastTime / (1 + hastePct / 100);
	}

	static double getHastePct(Character caster, AccumulatedCastStats castStats) {
		return getHastePct(caster, castStats.getHastePct(), castStats.getHasteRating());
	}

	static double getHastePct(Character caster, AccumulatedDurationStats durationStats) {
		return getHastePct(caster, durationStats.getHastePct(), durationStats.getHasteRating());
	}

	static double getHastePct(Character caster, double hastePct, double hasteRating) {
		var cr = caster.getCombatRatingInfo();
		var ratingHastePct = hasteRating / cr.getSpellHaste();

		return hastePct + ratingHastePct;
	}
}
