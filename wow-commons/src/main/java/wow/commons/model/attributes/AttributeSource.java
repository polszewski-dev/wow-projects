package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.spells.SpellSchool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface AttributeSource {
	Attributes getAttributes();

	default List<PrimitiveAttribute> getPrimitiveAttributeList() {
		return getAttributes().getPrimitiveAttributeList();
	}

	default Map<AttributeId, List<ComplexAttribute>> getComplexAttributeList() {
		return getAttributes().getComplexAttributeList();
	}

	default boolean isEmpty() {
		return getAttributes().isEmpty();
	}

	default double getDouble(AttributeId attributeId) {
		double result = 0;
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result += attribute.getDouble();
			}
		}
		return result;
	}

	private double getDouble(AttributeId attributeId, SpellSchool spellSchool) {
		double result = 0;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.isTheSameOrNull(spellSchool)) {
				result += attribute.getDouble();
			}
		}
		return result;
	}

	default Percent getPercent(AttributeId attributeId) {
		Percent result = Percent.ZERO;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result.add(attribute.getPercent());
			}
		}
		return result;
	}

	default boolean getBoolean(AttributeId attributeId) {
		boolean result = false;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result || attribute.getBoolean();
			}
		}
		return result;
	}

	default Duration getDuration(AttributeId attributeId) {
		Duration result = Duration.ZERO;
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result.add(attribute.getDuration());
			}
		}
		return result;
	}

	default <T extends ComplexAttribute> List<T> getList(AttributeId attributeId) {
		return (List)getComplexAttributeList().getOrDefault(attributeId, List.of());
	}

	default List<ComplexAttribute> getList(Collection<AttributeId> attributeIds) {
		List<ComplexAttribute> result = new ArrayList<>();
		var complexAttributeList = getComplexAttributeList();

		for (AttributeId attributeId : attributeIds) {
			result.addAll(complexAttributeList.getOrDefault(attributeId, List.of()));
		}

		return result;
	}

	default boolean scalesWithStacks() {
		return false;
	}

	default String statString() {
		return getAttributes().toString();
	}

	default double getStamina() {
		return getDouble(AttributeId.STAMINA);
	}

	default double getIntellect() {
		return getDouble(AttributeId.INTELLECT);
	}

	default double getSpirit() {
		return getDouble(AttributeId.SPIRIT);
	}

	default double getBaseStatsIncrease() {
		return getDouble(AttributeId.BASE_STATS_INCREASE);
	}

	default Percent getBaseStatsIncreasePct() {
		return getPercent(AttributeId.BASE_STATS_INCREASE_PCT);
	}

	default Percent getStaIncreasePct() {
		return getPercent(AttributeId.STA_INCREASE_PCT);
	}

	default Percent getIntIncreasePct() {
		return getPercent(AttributeId.INT_INCREASE_PCT);
	}

	default Percent getSpiIncreasePct() {
		return getPercent(AttributeId.SPI_INCREASE_PCT);
	}

	default double getSpellPower() {
		return getDouble(AttributeId.SPELL_POWER);
	}

	default double getSpellDamage() {
		return getDouble(AttributeId.SPELL_DAMAGE);
	}

	default double getSpellDamage(SpellSchool spellSchool) {
		return getDouble(AttributeId.SPELL_DAMAGE, spellSchool);
	}

	default double getTotalSpellDamage() {
		return getSpellPower() + getSpellDamage();
	}

	default double getTotalSpellDamage(SpellSchool spellSchool) {
		return getSpellPower() + getSpellDamage(spellSchool);
	}

	default double getSpellCritRating() {
		return getDouble(AttributeId.SPELL_CRIT_RATING);
	}

	default Percent getSpellCritPct() {
		return getPercent(AttributeId.SPELL_CRIT_PCT);
	}

	default Percent getIncreasedCriticalDamagePct() {
		return getPercent(AttributeId.INCREASED_CRITICAL_DAMAGE_PCT);
	}

	default Percent getCritDamageIncreasePct() {
		return getPercent(AttributeId.CRIT_DAMAGE_INCREASE_PCT);
	}

	default double getSpellHitRating() {
		return getDouble(AttributeId.SPELL_HIT_RATING);
	}

	default Percent getSpellHitPct() {
		return getPercent(AttributeId.SPELL_HIT_PCT);
	}

	default double getSpellHasteRating() {
		return getDouble(AttributeId.SPELL_HASTE_RATING);
	}

	default Percent getSpellHastePct() {
		return getPercent(AttributeId.SPELL_HASTE_PCT);
	}

	default double getSpellPenetration() {
		return getDouble(AttributeId.SPELL_PENETRATION);
	}

	default double getHealingPower() {
		return getDouble(AttributeId.HEALING_POWER);
	}

	default double getMp5() {
		return getDouble(AttributeId.MP5);
	}

	default double getResistance() {
		return getDouble(AttributeId.RESISTANCE);
	}

	default double getResistance(SpellSchool spellSchool) {
		return getDouble(AttributeId.RESISTANCE, spellSchool);
	}

	default Percent getDamageTakenPct() {
		return getPercent(AttributeId.DAMAGE_TAKEN_PCT);
	}

	default Percent getDirectDamageIncreasePct() {
		return getPercent(AttributeId.DIRECT_DAMAGE_INCREASE_PCT);
	}

	default Percent getDoTDamageIncreasePct() {
		return getPercent(AttributeId.DOT_DAMAGE_INCREASE_PCT);
	}

	default Percent getAdditionalSpellDamageTakenPct() {
		return getPercent(AttributeId.ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT);
	}

	default Percent getEffectIncreasePct() {
		return getPercent(AttributeId.EFFECT_INCREASE_PCT);
	}

	default Percent getDamageTakenIncreasePct() {
		return getPercent(AttributeId.DAMAGE_TAKEN_INCREASE_PCT);
	}

	default Percent getSpellCoeffPct() {
		return getPercent(AttributeId.SPELL_COEFF_BONUS_PCT);
	}

	default Duration getCastTimeReduction() {
		return getDuration(AttributeId.CAST_TIME_REDUCTION);
	}

	default Percent getCostReductionPct() {
		return getPercent(AttributeId.COST_REDUCTION_PCT);
	}

	default List<SpecialAbility> getSpecialAbilities() {
		return getList(AttributeId.SPECIAL_ABILITIES);
	}

	default double getExtraCritCoeff() {
		return getDouble(AttributeId.EXTRA_CRIT_COEFF);
	}

	default boolean hasCasterStats() {
		return hasCasterStats(null);
	}

	default boolean hasCasterStats(SpellSchool spellSchool) {
		return (getTotalSpellDamage(spellSchool) != 0 || getSpellCritRating() != 0 || getSpellHitRating() != 0 || getSpellHasteRating() != 0) && getHealingPower() == 0;
	}

	default Percent getThreatReductionPct() {
		return getPercent(AttributeId.THREAT_REDUCTION_PCT);
	}

	default Percent getSpeedIncreasePct() {
		return getPercent(AttributeId.SPEED_INCREASE_PCT);
	}
}
