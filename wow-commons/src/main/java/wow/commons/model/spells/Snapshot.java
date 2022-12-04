package wow.commons.model.spells;

import lombok.Getter;
import wow.commons.constants.SpellConstants;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.CombatRatingInfo;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-08-20
 */
@Getter
public class Snapshot implements StatProvider {
	private final Spell spell;
	private final BaseStatInfo baseStats;
	private final CombatRatingInfo cr;
	private final Attributes stats;

	private final Set<AttributeCondition> conditions;
	
	private double statsBaseStatsIncreasePct;
	private double statsStaIncreasePct;
	private double statsIntIncreasePct;
	private double statsSpiIncreasePct;
	private double statsStamina;
	private double statsIntellect;
	private double statsSpirit;
	private double statsBaseStatsIncrease;
	private double statsTotalSpellDamage;
	private double statsAdditionalSpellDamageTakenPct;
	private double statsDamageTakenPct;
	private double statsEffectIncreasePct;
	private double statsDirectDamageIncreasePct;
	private double statsDoTDamageIncreasePct;
	private double statsSpellHitRating;
	private double statsSpellHitPct;
	private double statsSpellCritRating;
	private double statsSpellCritPct;
	private double statsSpellHasteRating;
	private double statsSpellHastePct;
	private double statsIncreasedCriticalDamagePct;
	private double statsCritDamageIncreasePct;
	private double statsExtraCritCoeff;
	private double statsSpellCoeffPct;
	private double statsCastTimeReduction;
	private double statsCostReductionPct;

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

	public Snapshot(Spell spell, BaseStatInfo baseStats, CombatRatingInfo cr, Attributes stats, PetType activePet, CreatureType enemyType) {
		this.spell = spell;
		this.baseStats = baseStats;
		this.cr = cr;
		this.stats = stats;
		this.conditions = Stream.of(
				AttributeCondition.of(spell.getTalentTree()),
				AttributeCondition.of(spell.getSpellSchool()),
				AttributeCondition.of(spell.getSpellId()),
				AttributeCondition.of(activePet),
				AttributeCondition.of(enemyType),
				AttributeCondition.EMPTY
		).collect(Collectors.toSet());

		accumulateStats();
		calcBaseStats();
		calcSpellStats();
		calcCastTime();
		calcCost();

		this.calcFinished = true;
	}

	private void calcBaseStats() {
		double baseStaMultiplier = 1 + statsBaseStatsIncreasePct / 100.0 + statsStaIncreasePct / 100.0;
		double baseIntMultiplier = 1 + statsBaseStatsIncreasePct / 100.0 + statsIntIncreasePct / 100.0;
		double baseSpiMultiplier = 1 + statsBaseStatsIncreasePct / 100.0 + statsSpiIncreasePct / 100.0;

		stamina = (baseStats.getBaseStamina() + statsStamina + statsBaseStatsIncrease) * baseStaMultiplier;
		intellect = (baseStats.getBaseIntellect() + statsIntellect + statsBaseStatsIncrease) * baseIntMultiplier;
		spirit = (baseStats.getBaseSpirit() + statsSpirit + statsBaseStatsIncrease) * baseSpiMultiplier;
	}

	private void calcSpellStats() {
		totalHit = getTotalHit(cr);
		totalCrit = getTotalCrit(baseStats, cr);
		totalHaste = getTotalHaste(cr);

		sp = statsTotalSpellDamage;

		spMultiplier = 1 + statsAdditionalSpellDamageTakenPct / 100.0;

		hitChance = Math.min(SpellConstants.BASE_HIT.getValue() + totalHit, 99) / 100.0;
		critChance = Math.min(totalCrit, 100) / 100.0;
		haste = totalHaste / 100.0;

		critCoeff = getCritCoeffValue();

		directDamageDoneMultiplier = 1 + statsDamageTakenPct / 100.0 + statsEffectIncreasePct / 100.0 + statsDirectDamageIncreasePct / 100.0;
		dotDamageDoneMultiplier = 1 + statsDamageTakenPct / 100.0 + statsEffectIncreasePct / 100.0 + statsDoTDamageIncreasePct / 100.0;

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
		// crit chance not yet calculated, but we have all necessary values in place
		if (!calcFinished) {
			calcSpellStats();
		}
		return critChance;
	}

	private double getTotalHit(CombatRatingInfo cr) {
		double ratingHit = statsSpellHitRating / cr.getSpellHit();
		double pctHit = statsSpellHitPct;
		return ratingHit + pctHit;
	}

	private double getTotalCrit(BaseStatInfo baseStats, CombatRatingInfo cr) {
		double baseCrit = baseStats.getBaseSpellCritPct().getValue();
		double intCrit = (intellect - baseStats.getBaseIntellect()) / baseStats.getIntellectPerCritPct();
		double ratingCrit = statsSpellCritRating / cr.getSpellCrit();
		double pctCrit = statsSpellCritPct;
		return baseCrit + intCrit + ratingCrit + pctCrit;
	}

	private double getTotalHaste(CombatRatingInfo cr) {
		double ratingHaste = statsSpellHasteRating / cr.getSpellHaste();
		double pctHaste = statsSpellHastePct;
		return pctHaste + ratingHaste;
	}

	private double getCritCoeffValue() {
		double increasedCriticalDamage = statsIncreasedCriticalDamagePct / 100.0;
		double talentIncrease = statsCritDamageIncreasePct / 100.0;
		double extraCritCoeff = statsExtraCritCoeff;
		return 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;
	}

	private double getSpellCoeffDirectValue() {
		double baseSpellCoeffDirect = spell.getCoeffDirect().getCoefficient();
		double talentSpellCoeff = statsSpellCoeffPct / 100.0;
		return baseSpellCoeffDirect + talentSpellCoeff;
	}

	private double getSpellCoeffDoTValue() {
		double baseSpellCoeffDoT = spell.getCoeffDot().getCoefficient();
		double talentSpellCoeff = statsSpellCoeffPct / 100.0;
		return baseSpellCoeffDoT + talentSpellCoeff;
	}

	private void calcCastTime() {
		Duration castTimeReduction = Duration.seconds(statsCastTimeReduction);
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
		Percent costReductionPct = Percent.of(statsCostReductionPct);
		manaCost = baseManaCost * costReductionPct.negate().toMultiplier();
	}

	private void accumulateStats() {
		accumulatePrimitiveAttributes(stats.getPrimitiveAttributeList());
		solveAbilities();
	}

	private void accumulatePrimitiveAttributes(List<PrimitiveAttribute> attributes) {
		for (PrimitiveAttribute attribute : attributes) {
			if (conditions.contains(attribute.getCondition())) {
				accumulateAttribute(attribute);
			}
		}
	}

	private void accumulateAttribute(PrimitiveAttribute attribute) {
		switch (attribute.getId()) {
			case BASE_STATS_INCREASE_PCT:
				this.statsBaseStatsIncreasePct += attribute.getDouble();
				break;
			case STA_INCREASE_PCT:
				this.statsStaIncreasePct += attribute.getDouble();
				break;
			case INT_INCREASE_PCT:
				this.statsIntIncreasePct += attribute.getDouble();
				break;
			case SPI_INCREASE_PCT:
				this.statsSpiIncreasePct += attribute.getDouble();
				break;
			case STAMINA:
				this.statsStamina += attribute.getDouble();
				break;
			case INTELLECT:
				this.statsIntellect += attribute.getDouble();
				break;
			case SPIRIT:
				this.statsSpirit += attribute.getDouble();
				break;
			case BASE_STATS_INCREASE:
				this.statsBaseStatsIncrease += attribute.getDouble();
				break;
			case SPELL_POWER:
			case SPELL_DAMAGE:
				this.statsTotalSpellDamage += attribute.getDouble();
				break;
			case ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT:
				this.statsAdditionalSpellDamageTakenPct += attribute.getDouble();
				break;
			case DAMAGE_TAKEN_PCT:
				this.statsDamageTakenPct += attribute.getDouble();
				break;
			case EFFECT_INCREASE_PCT:
				this.statsEffectIncreasePct += attribute.getDouble();
				break;
			case DIRECT_DAMAGE_INCREASE_PCT:
				this.statsDirectDamageIncreasePct += attribute.getDouble();
				break;
			case DOT_DAMAGE_INCREASE_PCT:
				this.statsDoTDamageIncreasePct += attribute.getDouble();
				break;
			case SPELL_HIT_RATING:
				this.statsSpellHitRating += attribute.getDouble();
				break;
			case SPELL_HIT_PCT:
				this.statsSpellHitPct += attribute.getDouble();
				break;
			case SPELL_CRIT_RATING:
				this.statsSpellCritRating += attribute.getDouble();
				break;
			case SPELL_CRIT_PCT:
				this.statsSpellCritPct += attribute.getDouble();
				break;
			case SPELL_HASTE_RATING:
				this.statsSpellHasteRating += attribute.getDouble();
				break;
			case SPELL_HASTE_PCT:
				this.statsSpellHastePct += attribute.getDouble();
				break;
			case INCREASED_CRITICAL_DAMAGE_PCT:
				this.statsIncreasedCriticalDamagePct += attribute.getDouble();
				break;
			case CRIT_DAMAGE_INCREASE_PCT:
				this.statsCritDamageIncreasePct += attribute.getDouble();
				break;
			case EXTRA_CRIT_COEFF:
				this.statsExtraCritCoeff += attribute.getDouble();
				break;
			case SPELL_COEFF_BONUS_PCT:
				this.statsSpellCoeffPct += attribute.getDouble();
				break;
			case CAST_TIME_REDUCTION:
				this.statsCastTimeReduction += attribute.getDouble();
				break;
			case COST_REDUCTION_PCT:
				this.statsCostReductionPct += attribute.getDouble();
				break;
			default:
				// ignore the rest
		}
	}

	private void solveAbilities() {
		List<SpecialAbility> specialAbilities = new ArrayList<>(stats.getSpecialAbilities());
		specialAbilities.sort(Comparator.comparingInt(SpecialAbility::getPriority));

		for (SpecialAbility specialAbility : specialAbilities) {
			Attributes statEquivalent = specialAbility.getStatEquivalent(this);
			accumulatePrimitiveAttributes(statEquivalent.getPrimitiveAttributeList());
		}
	}

	public enum CritMode {
		ALWAYS,
		NEVER,
		AVERAGE
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
		// direct damage

		double directDamage = 0;

		if (spell.hasDirectComponent()) {
			int baseDmgMin = spell.getMinDmg();
			int baseDmgMax = spell.getMaxDmg();

			if (useBothDamageRanges) {
				baseDmgMin += spell.getMinDmg2();
				baseDmgMax += spell.getMaxDmg2();
			}

			double actualCritChance = getActualCritChance(critMode);

			directDamage += (baseDmgMin + baseDmgMax) / 2.0;
			directDamage += spellCoeffDirect * sp * spMultiplier;
			directDamage *= directDamageDoneMultiplier;
			directDamage *= hitChance;
			directDamage *= (1 - actualCritChance) * 1 + actualCritChance * critCoeff;
		}

		// dot damage

		double dotDamage = 0;

		if (spell.hasDotComponent()) {
			dotDamage = spell.getDotDmg();
			dotDamage += spellCoeffDoT * sp * spMultiplier;
			dotDamage *= dotDamageDoneMultiplier;
			dotDamage *= hitChance;
		}

		return directDamage + dotDamage;
	}

	private double getActualCritChance(CritMode critMode) {
		switch (critMode) {
			case ALWAYS:
				return 1;
			case NEVER:
				return 0;
			case AVERAGE:
				return critChance;
			default:
				throw new IllegalArgumentException("Unhandled value: " + critMode);
		}
	}
}
