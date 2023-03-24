package wow.character.model.snapshot;

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
	private double stamina;
	private double staminaPct;
	private double intellect;
	private double intellectPct;
	private double spirit;
	private double spiritPct;
	private double baseStats;
	private double baseStatsPct;
	private double spellDamage;
	private double spellDamagePct;
	private double hitRating;
	private double hitPct;
	private double critRating;
	private double critPct;
	private double hasteRating;
	private double hastePct;
	private double damagePct;
	private double effectIncreasePct;
	private double directDamagePct;
	private double dotDamagePct;
	private double critDamagePct;
	private double critDamageMultiplierPct;
	private double critCoeffPct;
	private double spellPowerCoeffPct;
	private double castTime;
	private double costPct;
	private double duration;
	private double durationPct;
	private double cooldown;
	private double cooldownPct;
	private double threatPct;
	private double pushbackPct;

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
			if (isRelevant(attribute)) {
				accumulateAttribute(attribute.getId(), attribute.getDouble());
			}
		}
	}

	private boolean isRelevant(PrimitiveAttribute attribute) {
		if (!conditions.contains(attribute.getCondition())) {
			return false;
		}

		PrimitiveAttributeId id = attribute.getId();

		return id.getPowerType().isSpellDamage() && !id.isPetAttribute();
	}

	private void accumulateAttribute(PrimitiveAttributeId id, double value) {
		switch (id) {
			case STAMINA:
				this.stamina += value;
				break;
			case STAMINA_PCT:
				this.staminaPct += value;
				break;
			case INTELLECT:
				this.intellect += value;
				break;
			case INTELLECT_PCT:
				this.intellectPct += value;
				break;
			case SPIRIT:
				this.spirit += value;
				break;
			case SPIRIT_PCT:
				this.spiritPct += value;
				break;
			case BASE_STATS:
				this.baseStats += value;
				break;
			case BASE_STATS_PCT:
				this.baseStatsPct += value;
				break;
			case SPELL_POWER, SPELL_DAMAGE:
				this.spellDamage += value;
				break;
			case SPELL_DAMAGE_PCT:
				this.spellDamagePct += value;
				break;
			case HIT_RATING, SPELL_HIT_RATING:
				this.hitRating += value;
				break;
			case HIT_PCT, SPELL_HIT_PCT:
				this.hitPct += value;
				break;
			case CRIT_RATING, SPELL_CRIT_RATING:
				this.critRating += value;
				break;
			case CRIT_PCT, SPELL_CRIT_PCT:
				this.critPct += value;
				break;
			case HASTE_RATING, SPELL_HASTE_RATING:
				this.hasteRating += value;
				break;
			case HASTE_PCT, SPELL_HASTE_PCT:
				this.hastePct += value;
				break;
			case DAMAGE_PCT:
				this.damagePct += value;
				break;
			case DIRECT_DAMAGE_PCT:
				this.directDamagePct += value;
				break;
			case DOT_DAMAGE_PCT:
				this.dotDamagePct += value;
				break;
			case CRIT_DAMAGE_PCT:
				this.critDamagePct += value;
				break;
			case CRIT_DAMAGE_MULTIPLIER_PCT:
				this.critDamageMultiplierPct += value;
				break;
			case CRIT_COEFF_PCT:
				this.critCoeffPct += value;
				break;
			case EFFECT_PCT:
				this.effectIncreasePct += value;
				break;
			case POWER_COEFFICIENT_PCT:
				this.spellPowerCoeffPct += value;
				break;
			case COST_PCT:
				this.costPct += value;
				break;
			case CAST_TIME:
				this.castTime += value;
				break;
			case DURATION:
				this.duration += value;
				break;
			case DURATION_PCT:
				this.durationPct += value;
				break;
			case COOLDOWN:
				this.cooldown += value;
				break;
			case COOLDOWN_PCT:
				this.cooldownPct += value;
				break;
			case THREAT_PCT:
				this.threatPct += value;
				break;
			case PUSHBACK_PCT:
				this.pushbackPct += value;
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
		return switch (attributeId) {
			case INTELLECT -> intellect;
			case PET_STAMINA, PET_INTELLECT -> 0; // pets not supported at this moment
			default -> throw new IllegalArgumentException("Unhandled attribute: " + attributeId);
		};
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
