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
	private boolean instantCast;

	private double manaCost;

	private double duration;
	private double cooldown;

	private double threatPct;
	private double pushbackPct;

	private final boolean calcFinished;

	public Snapshot(Spell spell, Character character, Attributes attributes) {
		this.spell = spell;
		this.baseStats = character.getBaseStatInfo();
		this.cr = character.getCombatRatingInfo();
		this.stats = new AccumulatedSpellStats(
				attributes,
				character.getConditions(spell)
		);

		stats.accumulateStats(this);

		calcBaseStats();
		calcSpellStats();
		calcCastTime();
		calcCost();
		calcDuration();
		calcCooldown();
		calcMisc();

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
		double changedCastTime = max(baseCastTime + castTimeChange, 0);

		castTime = changedCastTime / (1 + haste);
		gcd = max(GCD.getSeconds() / (1 + haste), MIN_GCD.getSeconds());
		effectiveCastTime = max(castTime, gcd);
		instantCast = castTime == 0;
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

	private void calcCooldown() {
		double originalCooldown = spell.getSpellInfo().getCooldown().getSeconds();

		if (stats.getCooldown() == 0 && stats.getCooldownPct() == 0) {
			this.cooldown = originalCooldown;
			return;
		}

		this.cooldown = addAndMultiplyByPct(
				originalCooldown,
				stats.getCooldown(),
				stats.getCooldownPct()
		);
	}

	private void calcDuration() {
		if (stats.getDuration() == 0 && stats.getDurationPct() == 0) {
			if (spell.hasDotComponent()) {
				this.duration = spell.getDotDuration().getSeconds();
			}
			return;
		}

		if (spell.isChanneled()) {
			throw new IllegalArgumentException("Channeled spell");
		}

		if (!spell.hasDotComponent()) {
			throw new IllegalArgumentException("No DoT component");
		}

		this.duration = addAndMultiplyByPct(
				spell.getDotDuration().getSeconds(),
				stats.getDuration(),
				stats.getDamagePct()
		);
	}

	private void calcMisc() {
		this.threatPct = max(100 + stats.getThreatPct(), 0);
		this.pushbackPct = min(max(100 + stats.getPushbackPct(), 0), 100);
	}

	private static double addAndMultiplyByPct(double value, double modifier, double modifierPct) {
		return max(value + modifier, 0) * max(1 + modifierPct / 100, 0);
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
		dotDamage *= 1 + getDoTDamageChange();
		return dotDamage;
	}

	private double getDoTDamageChange() {
		double durationChange = duration - spell.getDotDuration().getSeconds();
		double tickChange = (int)(durationChange / spell.getTickInterval().getSeconds());
		return tickChange / spell.getNumTicks();
	}
}
