package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedHitStats;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.spell.Spell;

import static java.lang.Math.clamp;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public class HitCalculations extends Calculations {
	public static AccumulatedHitStats newAccumulatedHitStats(Character caster, Spell spell, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpellDamage(caster, spell, target, null);

		return new AccumulatedHitStats(conditionArgs);
	}

	private static AccumulatedHitStats getAccumulatedHitStats(Character caster, Spell spell, Character target) {
		var hitStats = newAccumulatedHitStats(caster, spell, target);

		accumulateEffects(caster, hitStats);
		return hitStats;
	}

	public static double getSpellHitPct(Character caster, Spell spell, Character target) {
		var hitStats = getAccumulatedHitStats(caster, spell, target);

		return getSpellHitPct(caster, spell, target, hitStats);
	}

	public static double getSpellHitPct(Character caster, Spell spell, Character target, AccumulatedHitStats hitStats) {
		var levelDifference = Character.getLevelDifference(caster, target);

		return getSpellHitPct(caster, hitStats, levelDifference);
	}

	static double getSpellHitPct(Character caster, AccumulatedHitStats hitStats, int levelDifference) {
		var totalHit = getSpellHitPctBonus(caster, hitStats);

		var gameVersion = caster.getGameVersion();
		var baseSpellHitChancePct = gameVersion.getBaseSpellHitChancePct(levelDifference);
		var maxSpellHitChancePct = gameVersion.getMaxPveSpellHitChancePct();

		return clamp(baseSpellHitChancePct + totalHit, 0, maxSpellHitChancePct);
	}

	static double getSpellHitPctBonus(Character caster, AccumulatedHitStats hitStats) {
		var cr = caster.getCombatRatingInfo();
		var ratingHit = hitStats.getHitRating() / cr.getSpellHit();
		var pctHit = hitStats.getHitPct();

		return ratingHit + pctHit;
	}
}
