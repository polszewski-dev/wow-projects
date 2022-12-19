package wow.commons.model.spells;

import lombok.Getter;
import wow.commons.constants.SpellConstants;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.character.CombatRatingInfo;
import wow.commons.model.character.EnemyInfo;

/**
 * User: POlszewski
 * Date: 2021-08-20
 */
@Getter
public class Snapshot implements StatProvider {
	private final Spell spell;
	private final BaseStatInfo baseStats;
	private final CombatRatingInfo cr;

	private final AccumulatedSpellStats stats;

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

	private double spellCoeffDirect;
	private double spellCoeffDoT;

	private Duration castTime;// actual cast time
	private Duration gcd;
	private Duration effectiveCastTime;// max(castTime, gcd)

	private double manaCost;

	private final boolean calcFinished;

	public Snapshot(Spell spell, CharacterInfo characterInfo, EnemyInfo enemyInfo, Attributes attributes) {
		this.spell = spell;
		this.baseStats = characterInfo.getBaseStatInfo();
		this.cr = characterInfo.getCombatRatingInfo();
		this.stats = new AccumulatedSpellStats(
				attributes,
				spell.getConditions(characterInfo.getActivePet(), enemyInfo.getEnemyType())
		);

		stats.accumulateStats(this);

		calcBaseStats();
		calcSpellStats();
		calcCastTime();
		calcCost();

		this.calcFinished = true;
	}

	private void calcBaseStats() {
		double baseSta = baseStats.getBaseStamina() + stats.getBaseStatsIncrease() + stats.getStamina();
		double baseInt = baseStats.getBaseIntellect() + stats.getBaseStatsIncrease() + stats.getIntellect();
		double baseSpi = baseStats.getBaseSpirit() + stats.getBaseStatsIncrease() + stats.getSpirit();

		double baseStaMultiplier = 1 + (stats.getBaseStatsIncreasePct() + stats.getStaIncreasePct()) / 100;
		double baseIntMultiplier = 1 + (stats.getBaseStatsIncreasePct() + stats.getIntIncreasePct()) / 100;
		double baseSpiMultiplier = 1 + (stats.getBaseStatsIncreasePct() + stats.getSpiIncreasePct()) / 100;

		stamina = baseSta * baseStaMultiplier;
		intellect = baseInt * baseIntMultiplier;
		spirit = baseSpi * baseSpiMultiplier;
	}

	private void calcSpellStats() {
		totalHit = getTotalHitValue();
		totalCrit = getTotalCritValue();
		totalHaste = getTotalHasteValue();

		sp = stats.getTotalSpellDamage();

		spMultiplier = 1 + stats.getAdditionalSpellDamageTakenPct() / 100;

		hitChance = Math.min(SpellConstants.BASE_HIT.getValue() + totalHit, 99) / 100;
		critChance = Math.min(totalCrit, 100) / 100;
		haste = totalHaste / 100;

		critCoeff = getCritCoeffValue();

		directDamageDoneMultiplier = 1 + (stats.getDamageTakenPct() + stats.getEffectIncreasePct() + stats.getDirectDamageIncreasePct()) / 100;
		dotDamageDoneMultiplier = 1 + (stats.getDamageTakenPct() + stats.getEffectIncreasePct() + stats.getDotDamageIncreasePct()) / 100;

		spellCoeffDirect = getSpellCoeffDirectValue();
		spellCoeffDoT = getSpellCoeffDoTValue();
	}

	@Override
	public double getHitChance() {
		// this method can be called while solving special abilities
		// hit chance is not yet calculated, but we have all necessary values in place
		if (!calcFinished) {
			calcSpellStats();
		}
		return hitChance;
	}

	@Override
	public double getCritChance() {
		// this method can be called while solving special abilities
		// crit chance is not yet calculated, but we have all necessary values in place
		if (!calcFinished) {
			calcSpellStats();
		}
		return critChance;
	}

	private double getTotalHitValue() {
		double ratingHit = stats.getSpellHitRating() / cr.getSpellHit();
		double pctHit = stats.getSpellHitPct();
		return ratingHit + pctHit;
	}

	private double getTotalCritValue() {
		double baseCrit = baseStats.getBaseSpellCritPct().getValue();
		double intCrit = (intellect - baseStats.getBaseIntellect()) / baseStats.getIntellectPerCritPct();
		double ratingCrit = stats.getSpellCritRating() / cr.getSpellCrit();
		double pctCrit = stats.getSpellCritPct();
		return baseCrit + intCrit + ratingCrit + pctCrit;
	}

	private double getTotalHasteValue() {
		double ratingHaste = stats.getSpellHasteRating() / cr.getSpellHaste();
		double pctHaste = stats.getSpellHastePct();
		return pctHaste + ratingHaste;
	}

	private double getCritCoeffValue() {
		double increasedCriticalDamage = stats.getIncreasedCriticalDamagePct() / 100;
		double talentIncrease = stats.getCritDamageIncreasePct() / 100;
		double extraCritCoeff = stats.getExtraCritCoeff();
		return 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;
	}

	private double getSpellCoeffDirectValue() {
		double baseSpellCoeffDirect = spell.getCoeffDirect().getCoefficient();
		double talentSpellCoeff = stats.getSpellCoeffPct() / 100;
		return baseSpellCoeffDirect + talentSpellCoeff;
	}

	private double getSpellCoeffDoTValue() {
		double baseSpellCoeffDoT = spell.getCoeffDot().getCoefficient();
		double talentSpellCoeff = stats.getSpellCoeffPct() / 100;
		return baseSpellCoeffDoT + talentSpellCoeff;
	}

	private void calcCastTime() {
		Duration castTimeReduction = Duration.seconds(stats.getCastTimeReduction());
		Duration baseCastTime = spell.getCastTime();
		Duration reducedCastTime = baseCastTime.subtract(castTimeReduction);

		castTime =  reducedCastTime.divideBy(1 + haste);
		gcd = SpellConstants.GCD.divideBy(1 + haste).max(SpellConstants.MIN_GCD);
		effectiveCastTime = castTime.max(gcd);
	}

	@Override
	public Duration getEffectiveCastTime() {
		// this method can be called while solving special abilities
		// cast time is not yet calculated, but we have all necessary values in place
		if (!calcFinished) {
			calcCastTime();
		}
		return effectiveCastTime;
	}

	private void calcCost() {
		double baseManaCost = spell.getManaCost();
		Percent costReductionPct = Percent.of(stats.getCostReductionPct());
		manaCost = baseManaCost * costReductionPct.negate().toMultiplier();
	}

	public SpellStatistics getSpellStatistics(CritMode critMode, boolean useBothDamageRanges) {
		SpellStatistics result = new SpellStatistics();

		result.setSnapshot(this);
		result.setTotalDamage(getTotalDamage(critMode, useBothDamageRanges));
		result.setCastTime(effectiveCastTime);
		result.setDps(result.getTotalDamage() / result.getCastTime().getSeconds());
		result.setManaCost(manaCost);
		result.setDpm(result.getTotalDamage() / result.getManaCost());

		return result;
	}

	private double getTotalDamage(CritMode critMode, boolean useBothDamageRanges) {
		return getDirectDamage(critMode, useBothDamageRanges) + getDotDamage();
	}

	private double getDirectDamage(CritMode critMode, boolean useBothDamageRanges) {
		if (!spell.hasDirectComponent()) {
			return 0;
		}

		int baseDmgMin = spell.getMinDmg();
		int baseDmgMax = spell.getMaxDmg();

		if (useBothDamageRanges) {
			baseDmgMin += spell.getMinDmg2();
			baseDmgMax += spell.getMaxDmg2();
		}

		double actualCritChance = critMode.getActualCritChance(this);

		double directDamage = (baseDmgMin + baseDmgMax) / 2.0;
		directDamage += spellCoeffDirect * sp * spMultiplier;
		directDamage *= directDamageDoneMultiplier;
		directDamage *= hitChance;
		directDamage *= (1 - actualCritChance) * 1 + actualCritChance * critCoeff;
		return directDamage;
	}

	private double getDotDamage() {
		if (!spell.hasDotComponent()) {
			return 0;
		}

		double dotDamage = spell.getDotDmg();
		dotDamage += spellCoeffDoT * sp * spMultiplier;
		dotDamage *= dotDamageDoneMultiplier;
		dotDamage *= hitChance;
		return dotDamage;
	}
}
