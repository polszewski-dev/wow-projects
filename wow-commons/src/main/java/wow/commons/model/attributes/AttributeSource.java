package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;

import java.util.List;
import java.util.Map;

import static wow.commons.model.attributes.complex.ComplexAttributeId.SPECIAL_ABILITIES;
import static wow.commons.model.attributes.complex.ComplexAttributeId.STAT_CONVERSION;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

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

	default double getDouble(PrimitiveAttributeId attributeId, AttributeCondition condition) {
		double result = 0;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.getCondition().equals(condition)) {
				result += attribute.getDouble();
			}
		}
		return result;
	}

	default double getDouble(PrimitiveAttributeId attributeId) {
		return getDouble(attributeId, AttributeCondition.EMPTY);
	}

	default double getDouble(PrimitiveAttributeId attributeId, SpellSchool spellSchool) {
		return getDouble(attributeId, AttributeCondition.of(spellSchool));
	}

	default Percent getPercent(PrimitiveAttributeId attributeId, AttributeCondition condition) {
		Percent result = Percent.ZERO;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.getCondition().equals(condition)) {
				result = result.add(attribute.getPercent());
			}
		}
		return result;
	}

	default Percent getPercent(PrimitiveAttributeId attributeId) {
		return getPercent(attributeId, AttributeCondition.EMPTY);
	}

	default Percent getPercent(PrimitiveAttributeId attributeId, SpellSchool spellSchool) {
		return getPercent(attributeId, AttributeCondition.of(spellSchool));
	}

	default Duration getDuration(PrimitiveAttributeId attributeId, AttributeCondition condition) {
		Duration result = Duration.ZERO;
		for (PrimitiveAttribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.getCondition().equals(condition)) {
				result = result.add(attribute.getDuration());
			}
		}
		return result;
	}

	default Duration getDuration(PrimitiveAttributeId attributeId) {
		return getDuration(attributeId, AttributeCondition.EMPTY);
	}

	default Duration getDuration(PrimitiveAttributeId attributeId, SpellSchool spellSchool) {
		return getDuration(attributeId, AttributeCondition.of(spellSchool));
	}

	default Duration getDuration(PrimitiveAttributeId attributeId, SpellId spellId) {
		return getDuration(attributeId, AttributeCondition.of(spellId));
	}

	default <T extends ComplexAttribute> List<T> getList(ComplexAttributeId attributeId) {
		return (List)getComplexAttributeList().getOrDefault(attributeId, List.of());
	}

	default String statString() {
		return getAttributes().toString();
	}

	default double getArmor() {
		return getDouble(ARMOR);
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
		return getSpellPower() + getSpellDamage() + getSpellDamage(spellSchool);
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

	default Percent getDamageTakenPct(SpellSchool spellSchool) {
		return getPercent(DAMAGE_TAKEN_PCT, AttributeCondition.of(spellSchool));
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

	default Percent getEffectIncreasePct(SpellId spellId) {
		return getPercent(EFFECT_INCREASE_PCT, AttributeCondition.of(spellId));
	}

	default Percent getDamageTakenIncreasePct() {
		return getPercent(DAMAGE_TAKEN_INCREASE_PCT);
	}

	default Percent getSpellCoeffPct() {
		return getPercent(SPELL_COEFF_BONUS_PCT);
	}

	default Duration getCastTimeReduction(SpellId spellId) {
		return getDuration(CAST_TIME_REDUCTION, spellId);
	}

	default Percent getCostReductionPct() {
		return getPercent(COST_REDUCTION_PCT);
	}

	default List<SpecialAbility> getSpecialAbilities() {
		return getList(SPECIAL_ABILITIES);
	}

	default List<StatConversion> getStatConversions() {
		return getList(STAT_CONVERSION);
	}

	default double getExtraCritCoeff() {
		return getDouble(EXTRA_CRIT_COEFF);
	}

	default Percent getThreatReductionPct() {
		return getPercent(THREAT_REDUCTION_PCT);
	}

	default Percent getSpeedIncreasePct() {
		return getPercent(SPEED_INCREASE_PCT);
	}
}
