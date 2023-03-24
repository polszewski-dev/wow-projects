package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.spells.Spell;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static wow.commons.constants.SpellConstants.*;

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

	private double castTime;// actual cast time
	private double gcd;
	private double effectiveCastTime;// max(castTime, gcd)

	private double manaCost;

	private final boolean calcFinished;

	public Snapshot(Spell spell, Character character, Attributes attributes) {
		this.spell = spell;
		this.baseStats = character.getBaseStatInfo();
		this.cr = character.getCombatRatingInfo();
		this.stats = new AccumulatedSpellStats(
				attributes,
				spell.getConditions(character.getActivePet(), character.getTargetEnemy().getEnemyType())
		);

		stats.accumulateStats(this);

		calcBaseStats();
		calcSpellStats();
		calcCastTime();
		calcCost();

		this.calcFinished = true;
	}

	private void calcBaseStats() {
		double baseSta = baseStats.getBaseStamina() + stats.getBaseStats() + stats.getStamina();
		double baseInt = baseStats.getBaseIntellect() + stats.getBaseStats() + stats.getIntellect();
		double baseSpi = baseStats.getBaseSpirit() + stats.getBaseStats() + stats.getSpirit();

		double baseStaMultiplier = 1 + (stats.getBaseStatsPct() + stats.getStaminaPct()) / 100;
		double baseIntMultiplier = 1 + (stats.getBaseStatsPct() + stats.getIntellectPct()) / 100;
		double baseSpiMultiplier = 1 + (stats.getBaseStatsPct() + stats.getSpiritPct()) / 100;

		stamina = baseSta * baseStaMultiplier;
		intellect = baseInt * baseIntMultiplier;
		spirit = baseSpi * baseSpiMultiplier;
	}

	private void calcSpellStats() {
		totalHit = getTotalHitValue();
		totalCrit = getTotalCritValue();
		totalHaste = getTotalHasteValue();

		sp = stats.getSpellDamage();

		spMultiplier = 1 + stats.getSpellDamagePct() / 100;

		hitChance = min(BASE_HIT.getValue() + totalHit, 99) / 100;
		critChance = min(totalCrit, 100) / 100;
		haste = totalHaste / 100;

		critCoeff = getCritCoeffValue();

		directDamageDoneMultiplier = 1 + (stats.getEffectIncreasePct() + stats.getDamagePct() + stats.getDirectDamagePct()) / 100;
		dotDamageDoneMultiplier = 1 + (stats.getEffectIncreasePct() + stats.getDamagePct() + stats.getDotDamagePct()) / 100;

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
		double ratingHit = stats.getHitRating() / cr.getSpellHit();
		double pctHit = stats.getHitPct();
		return ratingHit + pctHit;
	}

	private double getTotalCritValue() {
		double baseCrit = baseStats.getBaseSpellCritPct().getValue();
		double intCrit = (intellect - baseStats.getBaseIntellect()) / baseStats.getIntellectPerCritPct();
		double ratingCrit = stats.getCritRating() / cr.getSpellCrit();
		double pctCrit = stats.getCritPct();
		return baseCrit + intCrit + ratingCrit + pctCrit;
	}

	private double getTotalHasteValue() {
		double ratingHaste = stats.getHasteRating() / cr.getSpellHaste();
		double pctHaste = stats.getHastePct();
		return pctHaste + ratingHaste;
	}

	private double getCritCoeffValue() {
		double increasedCriticalDamage = stats.getCritDamagePct() / 100;
		double talentIncrease = stats.getCritDamageMultiplierPct() / 100;
		double extraCritCoeff = stats.getCritCoeffPct();
		return 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;
	}

	private double getSpellCoeffDirectValue() {
		double baseSpellCoeffDirect = spell.getCoeffDirect().getCoefficient();
		double talentSpellCoeff = stats.getSpellPowerCoeffPct() / 100;
		return baseSpellCoeffDirect + talentSpellCoeff;
	}

	private double getSpellCoeffDoTValue() {
		double baseSpellCoeffDoT = spell.getCoeffDot().getCoefficient();
		double talentSpellCoeff = stats.getSpellPowerCoeffPct() / 100;
		return baseSpellCoeffDoT + talentSpellCoeff;
	}

	private void calcCastTime() {
		double castTimeChange = stats.getCastTime();
		double baseCastTime = spell.getCastTime().getSeconds();
		double changedCastTime = baseCastTime + castTimeChange;

		castTime = changedCastTime / (1 + haste);
		gcd = max(GCD.getSeconds() / (1 + haste), MIN_GCD.getSeconds());
		effectiveCastTime = max(castTime, gcd);
	}

	@Override
	public double getEffectiveCastTime() {
		// this method can be called while solving special abilities
		// cast time is not yet calculated, but we have all necessary values in place
		if (!calcFinished) {
			calcCastTime();
		}
		return effectiveCastTime;
	}

	private void calcCost() {
		double baseManaCost = spell.getManaCost();
		Percent costChangePct = Percent.of(stats.getCostPct());
		manaCost = baseManaCost * costChangePct.toMultiplier();
	}

	public SpellStatistics getSpellStatistics(CritMode critMode, boolean useBothDamageRanges) {
		SpellStatistics result = new SpellStatistics();

		result.setSnapshot(this);
		result.setTotalDamage(getTotalDamage(critMode, useBothDamageRanges));
		result.setCastTime(Duration.seconds(effectiveCastTime));
		result.setDps(result.getTotalDamage() / result.getCastTime().getSeconds());
		result.setManaCost(manaCost);
		result.setDpm(result.getTotalDamage() / result.getManaCost());

		return result;
	}

	public double getDps(CritMode critMode, boolean useBothDamageRanges) {
		double totalDamage = getTotalDamage(critMode, useBothDamageRanges);
		double effectiveCastTimeSeconds = effectiveCastTime;
		return totalDamage / effectiveCastTimeSeconds;
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
