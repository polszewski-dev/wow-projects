package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.*;
import wow.commons.model.spells.SpellSchool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static wow.commons.model.attributes.complex.ComplexAttributeId.SPECIAL_ABILITIES;
import static wow.commons.model.attributes.primitive.DoubleAttributeId.*;
import static wow.commons.model.attributes.primitive.DurationAttributeId.CAST_TIME_REDUCTION;
import static wow.commons.model.attributes.primitive.PercentAttributeId.*;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface AttributeSource {
	Attributes getAttributes();

	default List<PrimitiveAttribute> getPrimitiveAttributeList() {
		return getAttributes().getPrimitiveAttributeList();
	}

	default Map<ComplexAttributeId, List<ComplexAttribute>> getComplexAttributeList() {
		return getAttributes().getComplexAttributeList();
	}

	default boolean isEmpty() {
		return getAttributes().isEmpty();
	}

	default double getDouble(DoubleAttributeId attributeId) {
		double result = 0;
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result += attribute.getDouble();
			}
		}
		return result;
	}

	private double getDouble(DoubleAttributeId attributeId, SpellSchool spellSchool) {
		double result = 0;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.isTheSameOrNull(spellSchool)) {
				result += attribute.getDouble();
			}
		}
		return result;
	}

	default Percent getPercent(PercentAttributeId attributeId) {
		Percent result = Percent.ZERO;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result.add(attribute.getPercent());
			}
		}
		return result;
	}

	default boolean getBoolean(BooleanAttributeId attributeId) {
		boolean result = false;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result || attribute.getBoolean();
			}
		}
		return result;
	}

	default Duration getDuration(DurationAttributeId attributeId) {
		Duration result = Duration.ZERO;
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result.add(attribute.getDuration());
			}
		}
		return result;
	}

	default <T extends ComplexAttribute> List<T> getList(ComplexAttributeId attributeId) {
		return (List)getComplexAttributeList().getOrDefault(attributeId, List.of());
	}

	default List<ComplexAttribute> getList(ComplexAttributeId[] attributeIds) {
		List<ComplexAttribute> result = new ArrayList<>();
		var complexAttributeList = getComplexAttributeList();

		for (ComplexAttributeId attributeId : attributeIds) {
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
		return getDouble(STAMINA);
	}

	default double getIntellect() {
		return getDouble(INTELLECT);
	}

	default double getSpirit() {
		return getDouble(SPIRIT);
	}

	default double getBaseStatsIncrease() {
		return getDouble(BASE_STATS_INCREASE);
	}

	default Percent getBaseStatsIncreasePct() {
		return getPercent(BASE_STATS_INCREASE_PCT);
	}

	default Percent getStaIncreasePct() {
		return getPercent(STA_INCREASE_PCT);
	}

	default Percent getIntIncreasePct() {
		return getPercent(INT_INCREASE_PCT);
	}

	default Percent getSpiIncreasePct() {
		return getPercent(SPI_INCREASE_PCT);
	}

	default double getSpellPower() {
		return getDouble(SPELL_POWER);
	}

	default double getSpellDamage() {
		return getDouble(SPELL_DAMAGE);
	}

	default double getSpellDamage(SpellSchool spellSchool) {
		return getDouble(SPELL_DAMAGE, spellSchool);
	}

	default double getTotalSpellDamage() {
		return getSpellPower() + getSpellDamage();
	}

	default double getTotalSpellDamage(SpellSchool spellSchool) {
		return getSpellPower() + getSpellDamage(spellSchool);
	}

	default double getSpellCritRating() {
		return getDouble(SPELL_CRIT_RATING);
	}

	default Percent getSpellCritPct() {
		return getPercent(SPELL_CRIT_PCT);
	}

	default Percent getIncreasedCriticalDamagePct() {
		return getPercent(INCREASED_CRITICAL_DAMAGE_PCT);
	}

	default Percent getCritDamageIncreasePct() {
		return getPercent(CRIT_DAMAGE_INCREASE_PCT);
	}

	default double getSpellHitRating() {
		return getDouble(SPELL_HIT_RATING);
	}

	default Percent getSpellHitPct() {
		return getPercent(SPELL_HIT_PCT);
	}

	default double getSpellHasteRating() {
		return getDouble(SPELL_HASTE_RATING);
	}

	default Percent getSpellHastePct() {
		return getPercent(SPELL_HASTE_PCT);
	}

	default double getSpellPenetration() {
		return getDouble(SPELL_PENETRATION);
	}

	default double getHealingPower() {
		return getDouble(HEALING_POWER);
	}

	default double getMp5() {
		return getDouble(MP5);
	}

	default double getResistance() {
		return getDouble(RESISTANCE);
	}

	default double getResistance(SpellSchool spellSchool) {
		return getDouble(RESISTANCE, spellSchool);
	}

	default Percent getDamageTakenPct() {
		return getPercent(DAMAGE_TAKEN_PCT);
	}

	default Percent getDirectDamageIncreasePct() {
		return getPercent(DIRECT_DAMAGE_INCREASE_PCT);
	}

	default Percent getDoTDamageIncreasePct() {
		return getPercent(DOT_DAMAGE_INCREASE_PCT);
	}

	default Percent getAdditionalSpellDamageTakenPct() {
		return getPercent(ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT);
	}

	default Percent getEffectIncreasePct() {
		return getPercent(EFFECT_INCREASE_PCT);
	}

	default Percent getDamageTakenIncreasePct() {
		return getPercent(DAMAGE_TAKEN_INCREASE_PCT);
	}

	default Percent getSpellCoeffPct() {
		return getPercent(SPELL_COEFF_BONUS_PCT);
	}

	default Duration getCastTimeReduction() {
		return getDuration(CAST_TIME_REDUCTION);
	}

	default Percent getCostReductionPct() {
		return getPercent(COST_REDUCTION_PCT);
	}

	default List<SpecialAbility> getSpecialAbilities() {
		return getList(SPECIAL_ABILITIES);
	}

	default double getExtraCritCoeff() {
		return getDouble(EXTRA_CRIT_COEFF);
	}

	default boolean hasCasterStats() {
		return hasCasterStats(null);
	}

	default boolean hasCasterStats(SpellSchool spellSchool) {
		return (getTotalSpellDamage(spellSchool) != 0 || getSpellCritRating() != 0 || getSpellHitRating() != 0 || getSpellHasteRating() != 0) && getHealingPower() == 0;
	}

	default Percent getThreatReductionPct() {
		return getPercent(THREAT_REDUCTION_PCT);
	}

	default Percent getSpeedIncreasePct() {
		return getPercent(SPEED_INCREASE_PCT);
	}
}
