package wow.commons.util;

import lombok.Data;
import wow.commons.constants.SpellConstants;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellRankInfo;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CombatRatingInfo;

/**
 * User: POlszewski
 * Date: 2021-08-20
 */
@Data
public class Snapshot {
	private SpellInfo spellInfo;
	private SpellRankInfo spellRankInfo;
	private Attributes stats;

	private double stamina;
	private double intellect;
	private double spirit;

	private double totalCrit;
	private double totalHit;
	private double totalHaste;

	private double sp;
	private double spMultiplier;

	private double hitChance;
	private double critChance;
	private double critCoeff;
	private double haste;

	private double directDamageDoneMultiplier;
	private double dotDamageDoneMultiplier;

	private Duration castTime;// actual cast time
	private Duration gcd;
	private Duration effectiveCastTime;// max(castTime, gcd)

	private double spellCoeffDirect;
	private double spellCoeffDoT;

	private double manaCost;

	public Snapshot(SpellInfo spellInfo, SpellRankInfo spellRankInfo, BaseStatInfo baseStats, CombatRatingInfo cr, Attributes stats) {
		this.spellInfo = spellInfo;
		this.spellRankInfo = spellRankInfo;
		this.stats = AttributesBuilder.filter(stats, spellInfo.getAttributeFiter());

		calcBaseStats(baseStats);
		calcSpellStats(spellInfo, baseStats, cr);
		calcCastTime(spellRankInfo);
		calcCost(spellRankInfo);
	}

	private void calcBaseStats(BaseStatInfo baseStats) {
		double baseStaMultiplier = 1 + stats.getBaseStatsIncreasePct().getCoefficient() + stats.getStaIncreasePct().getCoefficient();
		double baseIntMultiplier = 1 + stats.getBaseStatsIncreasePct().getCoefficient() + stats.getIntIncreasePct().getCoefficient();
		double baseSpiMultiplier = 1 + stats.getBaseStatsIncreasePct().getCoefficient() + stats.getSpiIncreasePct().getCoefficient();

		stamina = (baseStats.getBaseStamina() + stats.getStamina() + stats.getBaseStatsIncrease()) * baseStaMultiplier;
		intellect = (baseStats.getBaseIntellect() + stats.getIntellect() + stats.getBaseStatsIncrease()) * baseIntMultiplier;
		spirit = (baseStats.getBaseSpirit() + stats.getSpirit() + stats.getBaseStatsIncrease()) * baseSpiMultiplier;
	}

	private void calcSpellStats(SpellInfo spellInfo, BaseStatInfo baseStats, CombatRatingInfo cr) {
		totalHit = getTotalHit(cr);
		totalCrit = getTotalCrit(baseStats, cr);
		totalHaste = getTotalHaste(cr);

		sp = stats.getTotalSpellDamage();

		spMultiplier = 1 + stats.getAdditionalSpellDamageTakenPct().getCoefficient();

		hitChance = Math.min(SpellConstants.BASE_HIT.getValue() + totalHit, 99) / 100.0;
		critChance = Math.min(totalCrit, 100) / 100.0;
		haste = totalHaste / 100.0;

		critCoeff = getCritCoeffValue();

		directDamageDoneMultiplier = 1 + stats.getDamageTakenPct().getCoefficient() + stats.getEffectIncreasePct().getCoefficient() + stats.getDirectDamageIncreasePct().getCoefficient();
		dotDamageDoneMultiplier = 1 + stats.getDamageTakenPct().getCoefficient() + stats.getEffectIncreasePct().getCoefficient() + stats.getDoTDamageIncreasePct().getCoefficient();

		spellCoeffDirect = getSpellCoeffDirect(spellInfo);
		spellCoeffDoT = getSpellCoeffDoT(spellInfo);
	}

	private double getTotalHit(CombatRatingInfo cr) {
		double ratingHit = stats.getSpellHitRating() / cr.getSpellHit();
		double pctHit = stats.getSpellHitPct().getValue();
		return ratingHit + pctHit;
	}

	private double getTotalCrit(BaseStatInfo baseStats, CombatRatingInfo cr) {
		double intCrit = baseStats.getBaseSpellCrit() + (intellect - baseStats.getBaseIntellect()) / baseStats.getIntellectPerCritPct();
		double ratingCrit = stats.getSpellCritRating() / cr.getSpellCrit();
		double pctCrit = stats.getSpellCritPct().getValue();
		return intCrit + ratingCrit + pctCrit;
	}

	private double getTotalHaste(CombatRatingInfo cr) {
		double ratingHaste = stats.getSpellHasteRating() / cr.getSpellHaste();
		double pctHaste = stats.getSpellHastePct().getValue();
		return pctHaste + ratingHaste;
	}

	private double getCritCoeffValue() {
		double increasedCriticalDamage = stats.getIncreasedCriticalDamagePct().getCoefficient();
		double talentIncrease = stats.getCritDamageIncreasePct().getCoefficient();
		double extraCritCoeff = stats.getExtraCritCoeff();
		return 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;
	}

	private double getSpellCoeffDirect(SpellInfo spellInfo) {
		double baseSpellCoeffDirect = spellInfo.getCoeffDirect().getCoefficient();
		double talentSpellCoeff = stats.getSpellCoeffPct().getCoefficient();
		return baseSpellCoeffDirect + talentSpellCoeff;
	}

	private double getSpellCoeffDoT(SpellInfo spellInfo) {
		double baseSpellCoeffDoT = spellInfo.getCoeffDot().getCoefficient();
		double talentSpellCoeff = stats.getSpellCoeffPct().getCoefficient();
		return baseSpellCoeffDoT + talentSpellCoeff;
	}

	private void calcCastTime(SpellRankInfo spellRankInfo) {
		Duration castTimeReduction = stats.getCastTimeReduction();
		Duration baseCastTime = spellRankInfo.getCastTime();
		Duration reducedCastTime = baseCastTime.subtract(castTimeReduction);

		castTime = reducedCastTime.divideBy(1 + haste);
		gcd = SpellConstants.GCD.divideBy(1 + haste).max(SpellConstants.MIN_GCD);
		effectiveCastTime = castTime.max(gcd);
	}

	private void calcCost(SpellRankInfo spellRankInfo) {
		double baseManaCost = spellRankInfo.getManaCost();
		Percent costReductionPct = stats.getCostReductionPct();
		manaCost = baseManaCost * costReductionPct.negate().toMultiplier();
	}
}
