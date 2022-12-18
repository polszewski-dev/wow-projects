package wow.commons.model.spells;

import lombok.Getter;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@Getter
public class AccumulatedSpellStats {
	private double baseStatsIncreasePct;
	private double staIncreasePct;
	private double intIncreasePct;
	private double spiIncreasePct;
	private double stamina;
	private double intellect;
	private double spirit;
	private double baseStatsIncrease;
	private double totalSpellDamage;
	private double additionalSpellDamageTakenPct;
	private double damageTakenPct;
	private double effectIncreasePct;
	private double directDamageIncreasePct;
	private double dotDamageIncreasePct;
	private double spellHitRating;
	private double spellHitPct;
	private double spellCritRating;
	private double spellCritPct;
	private double spellHasteRating;
	private double spellHastePct;
	private double increasedCriticalDamagePct;
	private double critDamageIncreasePct;
	private double extraCritCoeff;
	private double spellCoeffPct;
	private double castTimeReduction;
	private double costReductionPct;

	private final Attributes attributes;
	private final Set<AttributeCondition> conditions;

	public AccumulatedSpellStats(Attributes attributes, Set<AttributeCondition> conditions) {
		this.attributes = attributes;
		this.conditions = conditions;
	}

	public void accumulateStats(StatProvider statProvider) {
		accumulatePrimitiveAttributes(attributes.getPrimitiveAttributeList());
		solveStatConversions();
		solveAbilities(statProvider);
	}

	private void accumulatePrimitiveAttributes(List<PrimitiveAttribute> attributes) {
		for (PrimitiveAttribute attribute : attributes) {
			if (conditions.contains(attribute.getCondition())) {
				accumulateAttribute(attribute.getId(), attribute.getDouble());
			}
		}
	}

	private void accumulateAttribute(PrimitiveAttributeId id, double value) {
		switch (id) {
			case BASE_STATS_INCREASE_PCT:
				this.baseStatsIncreasePct += value;
				break;
			case STA_INCREASE_PCT:
				this.staIncreasePct += value;
				break;
			case INT_INCREASE_PCT:
				this.intIncreasePct += value;
				break;
			case SPI_INCREASE_PCT:
				this.spiIncreasePct += value;
				break;
			case STAMINA:
				this.stamina += value;
				break;
			case INTELLECT:
				this.intellect += value;
				break;
			case SPIRIT:
				this.spirit += value;
				break;
			case BASE_STATS_INCREASE:
				this.baseStatsIncrease += value;
				break;
			case SPELL_POWER:
			case SPELL_DAMAGE:
				this.totalSpellDamage += value;
				break;
			case ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT:
				this.additionalSpellDamageTakenPct += value;
				break;
			case DAMAGE_TAKEN_PCT:
				this.damageTakenPct += value;
				break;
			case EFFECT_INCREASE_PCT:
				this.effectIncreasePct += value;
				break;
			case DIRECT_DAMAGE_INCREASE_PCT:
				this.directDamageIncreasePct += value;
				break;
			case DOT_DAMAGE_INCREASE_PCT:
				this.dotDamageIncreasePct += value;
				break;
			case SPELL_HIT_RATING:
				this.spellHitRating += value;
				break;
			case SPELL_HIT_PCT:
				this.spellHitPct += value;
				break;
			case SPELL_CRIT_RATING:
				this.spellCritRating += value;
				break;
			case SPELL_CRIT_PCT:
				this.spellCritPct += value;
				break;
			case SPELL_HASTE_RATING:
				this.spellHasteRating += value;
				break;
			case SPELL_HASTE_PCT:
				this.spellHastePct += value;
				break;
			case INCREASED_CRITICAL_DAMAGE_PCT:
				this.increasedCriticalDamagePct += value;
				break;
			case CRIT_DAMAGE_INCREASE_PCT:
				this.critDamageIncreasePct += value;
				break;
			case EXTRA_CRIT_COEFF:
				this.extraCritCoeff += value;
				break;
			case SPELL_COEFF_BONUS_PCT:
				this.spellCoeffPct += value;
				break;
			case CAST_TIME_REDUCTION:
				this.castTimeReduction += value;
				break;
			case COST_REDUCTION_PCT:
				this.costReductionPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	private void solveStatConversions() {
		List<StatConversion> statConversions = attributes.getStatConversions();

		for (StatConversion statConversion : statConversions) {
			double fromAmount = getAccumulatedValue(statConversion.getFromStat());
			double toAmount = fromAmount * statConversion.getRatioPct().getCoefficient();

			accumulateAttribute(statConversion.getToStat(), toAmount);
		}
	}

	private double getAccumulatedValue(PrimitiveAttributeId attributeId) {
		switch (attributeId) {
			case INTELLECT:
				return intellect;
			case PET_STAMINA:
			case PET_INTELLECT:
				return 0; // pets not supported at this moment
			default:
				throw new IllegalArgumentException("Unhandled attribute: " + attributeId);
		}
	}

	private void solveAbilities(StatProvider statProvider) {
		List<SpecialAbility> specialAbilities = new ArrayList<>(attributes.getSpecialAbilities());
		specialAbilities.sort(Comparator.comparingInt(SpecialAbility::getPriority));

		for (SpecialAbility specialAbility : specialAbilities) {
			Attributes statEquivalent = specialAbility.getStatEquivalent(statProvider);
			accumulatePrimitiveAttributes(statEquivalent.getPrimitiveAttributeList());
		}
	}
}
