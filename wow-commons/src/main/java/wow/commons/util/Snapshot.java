package wow.commons.util;

import wow.commons.constants.SpellConstants;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellRankInfo;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;

/**
 * User: POlszewski
 * Date: 2021-08-20
 */
public class Snapshot {
	public final SpellInfo spellInfo;
	public final SpellRankInfo spellRankInfo;
	public final Attributes stats;

	public final double stamina;
	public final double intellect;
	public final double spirit;

	public final double totalCrit;
	public final double totalHit;
	public final double totalHaste;

	public final double sp;
	public final double spMultiplier;

	public final double hitChance;
	public final double critChance;
	public final double critCoeff;
	public final double haste;

	public final double directDamageDoneMultiplier;
	public final double dotDamageDoneMultiplier;

	public final Duration castTime;// actual cast time
	public final Duration gcd;
	public final Duration effectiveCastTime;// max(castTime, gcd)

	public final double spellCoeffDirect;
	public final double spellCoeffDoT;

	public final double manaCost;

	public Snapshot(SpellInfo spellInfo, SpellRankInfo spellRankInfo, BaseStatInfo baseStats, CombatRatingInfo cr, Attributes stats) {
		this.spellInfo = spellInfo;
		this.spellRankInfo = spellRankInfo;

		AttributeCondition filter = AttributeCondition.of(spellInfo, null, null);

		stats = AttributesBuilder.filter(stats, filter);

		this.stats = stats;

		double baseStaMultiplier = 1 + stats.getBaseStatsIncreasePct().getCoefficient() + stats.getStaIncreasePct().getCoefficient();
		double baseIntMultiplier = 1 + stats.getBaseStatsIncreasePct().getCoefficient() + stats.getIntIncreasePct().getCoefficient();
		double baseSpiMultiplier = 1 + stats.getBaseStatsIncreasePct().getCoefficient() + stats.getSpiIncreasePct().getCoefficient();

		stamina = (baseStats.getBaseStamina() + stats.getStamina() + stats.getBaseStatsIncrease()) * baseStaMultiplier;
		intellect = (baseStats.getBaseIntellect() + stats.getIntellect() + stats.getBaseStatsIncrease()) * baseIntMultiplier;
		spirit = (baseStats.getBaseSpirit() + stats.getSpirit() + stats.getBaseStatsIncrease()) * baseSpiMultiplier;

		double ratingHit = stats.getSpellHitRating() / cr.getSpellHit();
		double pctHit = stats.getSpellHitPct().getValue();
		totalHit = ratingHit + pctHit;

		double intCrit = baseStats.getBaseSpellCrit() + (intellect - baseStats.getBaseIntellect()) / baseStats.getIntellectPerCritPct();
		double ratingCrit = stats.getSpellCritRating() / cr.getSpellCrit();
		double pctCrit = stats.getSpellCritPct().getValue();
		totalCrit = intCrit + ratingCrit + pctCrit;

		double ratingHaste = stats.getSpellHasteRating() / cr.getSpellHaste();
		double pctHaste = stats.getSpellHastePct().getValue();
		totalHaste = pctHaste + ratingHaste;

		sp = stats.getTotalSpellDamage();

		spMultiplier = 1 + stats.getAdditionalSpellDamageTakenPct().getCoefficient();

		hitChance = Math.min(SpellConstants.BASE_HIT + totalHit, 99) / 100.0;
		critChance = Math.min(totalCrit, 100) / 100.0;
		haste = totalHaste / 100.0;

		double increasedCriticalDamage = stats.getIncreasedCriticalDamagePct().getCoefficient();
		double talentIncrease = stats.getCritDamageIncreasePct().getCoefficient();
		double extraCritCoeff = stats.getExtraCritCoeff();
		critCoeff = 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;

		directDamageDoneMultiplier = 1 + stats.getDamageTakenPct().getCoefficient() + stats.getEffectIncreasePct().getCoefficient() + stats.getDirectDamageIncreasePct().getCoefficient();
		dotDamageDoneMultiplier = 1 + stats.getDamageTakenPct().getCoefficient() + stats.getEffectIncreasePct().getCoefficient() + stats.getDoTDamageIncreasePct().getCoefficient();

		double baseSpellCoeffDirect = spellInfo.getCoeffDirect().getCoefficient();
		double baseSpellCoeffDoT = spellInfo.getCoeffDot().getCoefficient();
		double talentSpellCoeff = stats.getSpellCoeffPct().getCoefficient();
		spellCoeffDirect = baseSpellCoeffDirect + talentSpellCoeff;
		spellCoeffDoT = baseSpellCoeffDoT + talentSpellCoeff;

		Duration castTimeReduction = stats.getCastTimeReduction();
		Duration baseCastTime = spellRankInfo.getCastTime();
		Duration reducedCastTime = baseCastTime.subtract(castTimeReduction);

		castTime = reducedCastTime.divideBy(1 + haste);
		gcd = SpellConstants.GCD.divideBy(1 + haste).max(SpellConstants.MIN_GCD);
		effectiveCastTime = castTime.max(gcd);

		double baseManaCost = spellRankInfo.getManaCost();
		Percent costReductionPct = stats.getCostReductionPct();
		manaCost = baseManaCost * costReductionPct.negate().toMultiplier();
	}
}
