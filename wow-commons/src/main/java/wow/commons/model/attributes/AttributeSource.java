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

	default double getStrength() {
		return getDouble(STRENGTH);
	}

	default Percent getStrengthPct() {
		return getPercent(STRENGTH_PCT);
	}

	default double getAgility() {
		return getDouble(AGILITY);
	}

	default Percent getAgilityPct() {
		return getPercent(AGILITY_PCT);
	}

	default double getStamina() {
		return getDouble(STAMINA);
	}

	default Percent getStaminaPct() {
		return getPercent(STAMINA_PCT);
	}

	default double getIntellect() {
		return getDouble(INTELLECT);
	}

	default Percent getIntellectPct() {
		return getPercent(INTELLECT_PCT);
	}

	default double getSpirit() {
		return getDouble(SPIRIT);
	}

	default Percent getSpiritPct() {
		return getPercent(SPIRIT_PCT);
	}

	default double getBaseStats() {
		return getDouble(BASE_STATS);
	}

	default Percent getBaseStatsPct() {
		return getPercent(BASE_STATS_PCT);
	}

	default double getAttackPower() {
		return getDouble(ATTACK_POWER);
	}

	default double getRangedAttackPower() {
		return getDouble(RANGED_ATTACK_POWER);
	}

	default double getSpellPower() {
		return getDouble(SPELL_POWER);
	}

	default double getSpellDamage() {
		return getDouble(SPELL_DAMAGE);
	}

	default Percent getSpellDamagePct() {
		return getPercent(SPELL_DAMAGE_PCT);
	}

	default double getHealingPower() {
		return getDouble(HEALING_POWER);
	}

	default Percent getHitPct() {
		return getPercent(HIT_PCT);
	}

	default double getHitRating() {
		return getDouble(HIT_RATING);
	}

	default double getPhysicalHitRating() {
		return getDouble(PHYSICAL_HIT_RATING);
	}

	default Percent getPhysicalHitPct() {
		return getPercent(PHYSICAL_HIT_PCT);
	}

	default double getSpellHitRating() {
		return getDouble(SPELL_HIT_RATING);
	}

	default Percent getSpellHitPct() {
		return getPercent(SPELL_HIT_PCT);
	}

	default double getCritRating() {
		return getDouble(CRIT_RATING);
	}

	default Percent getCritPct() {
		return getPercent(CRIT_PCT);
	}

	default double getPhysicalCritRating() {
		return getDouble(PHYSICAL_CRIT_RATING);
	}

	default Percent getPhysicalCritPct() {
		return getPercent(PHYSICAL_CRIT_PCT);
	}

	default double getSpellCritRating() {
		return getDouble(SPELL_CRIT_RATING);
	}

	default Percent getSpellCritPct() {
		return getPercent(SPELL_CRIT_PCT);
	}

	default Percent getHastePct() {
		return getPercent(HASTE_PCT);
	}

	default double getHasteRating() {
		return getDouble(HASTE_RATING);
	}

	default double getPhysicalHasteRating() {
		return getDouble(PHYSICAL_HASTE_RATING);
	}

	default Percent getPhysicalHastePct() {
		return getPercent(PHYSICAL_HASTE_PCT);
	}

	default double getSpellHasteRating() {
		return getDouble(SPELL_HASTE_RATING);
	}

	default Percent getSpellHastePct() {
		return getPercent(SPELL_HASTE_PCT);
	}

	default Percent getDamagePct() {
		return getPercent(DAMAGE_PCT);
	}

	default Percent getDirectDamagePct() {
		return getPercent(DIRECT_DAMAGE_PCT);
	}

	default Percent getDoTDamagePct() {
		return getPercent(DOT_DAMAGE_PCT);
	}

	default Percent getCritDamagePct() {
		return getPercent(CRIT_DAMAGE_PCT);
	}

	default Percent getCritMultiplierPct() {
		return getPercent(CRIT_DAMAGE_MULTIPLIER_PCT);
	}

	default Percent getCritCoeffPct() {
		return getPercent(CRIT_COEFF_PCT);
	}

	default Percent getEffectPct() {
		return getPercent(EFFECT_PCT);
	}

	default Percent getPowerCoefficientPct() {
		return getPercent(POWER_COEFFICIENT_PCT);
	}

	default double getArmorPenetration() {
		return getDouble(ARMOR_PENETRATION);
	}

	default double getSpellPenetration() {
		return getDouble(SPELL_PENETRATION);
	}

	default double getExpertiseRating() {
		return getDouble(EXPERTISE_RATING);
	}

	default double getMp5() {
		return getDouble(MP5);
	}

	default double getHp5() {
		return getDouble(HP5);
	}

	default double getArmor() {
		return getDouble(ARMOR);
	}

	default double getDodgeRating() {
		return getDouble(DODGE_RATING);
	}

	default double getDefense() {
		return getDouble(DEFENSE);
	}

	default double getDefenseRating() {
		return getDouble(DEFENSE_RATING);
	}

	default double getBlock() {
		return getDouble(BLOCK);
	}

	default double getBlockRating() {
		return getDouble(BLOCK_RATING);
	}

	default Percent getBlockPct() {
		return getPercent(BLOCK_PCT);
	}

	default double getShieldBlock() {
		return getDouble(SHIELD_BLOCK);
	}

	default double getShieldBlockRating() {
		return getDouble(SHIELD_BLOCK_RATING);
	}

	default double getParryRating() {
		return getDouble(PARRY_RATING);
	}

	default double getResilienceRating() {
		return getDouble(RESILIENCE_RATING);
	}

	default double getResistance() {
		return getDouble(RESISTANCE);
	}

	default Percent getSpeedPct() {
		return getPercent(SPEEED_PCT);
	}

	default Percent getCostPct() {
		return getPercent(COST_PCT);
	}

	default Duration getCastTime() {
		return getDuration(CAST_TIME);
	}

	default Percent getThreatPct() {
		return getPercent(THREAT_PCT);
	}

	default Percent getPushbackPct() {
		return getPercent(PUSHBACK_PCT);
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

	default double getResistance(SpellSchool spellSchool) {
		return getDouble(RESISTANCE, spellSchool);
	}

	default Percent getDamagePct(SpellSchool spellSchool) {
		return getPercent(DAMAGE_PCT, AttributeCondition.of(spellSchool));
	}

	default Duration getCastTime(SpellId spellId) {
		return getDuration(CAST_TIME, spellId);
	}

	default List<SpecialAbility> getSpecialAbilities() {
		return getList(SPECIAL_ABILITIES);
	}

	default List<StatConversion> getStatConversions() {
		return getList(STAT_CONVERSION);
	}
}
