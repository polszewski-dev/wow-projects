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
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.isTheSameOrNull(spellSchool)) {
				result += attribute.getDouble();
			}
		}
		return result;
	}

	default Percent getPercent(AttributeId attributeId) {
		Percent result = Percent.ZERO;
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId) {
				result = result.add(attribute.getPercent());
			}
		}
		return result;
	}

	private Percent getPercent(AttributeId attributeId, SpellSchool spellSchool) {
		Percent result = Percent.ZERO;
		for (Attribute attribute : getPrimitiveAttributeList()) {
			if (attribute.getId() == attributeId && attribute.isTheSameOrNull(spellSchool)) {
				result = result.add(attribute.getPercent());
			}
		}
		return result;
	}

	default boolean getBoolean(AttributeId attributeId) {
		boolean result = false;
		for (Attribute attribute : getPrimitiveAttributeList()) {
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
		return getDouble(AttributeId.Stamina);
	}

	default double getIntellect() {
		return getDouble(AttributeId.Intellect);
	}

	default double getSpirit() {
		return getDouble(AttributeId.Spirit);
	}

	default double getBaseStatsIncrease() {
		return getDouble(AttributeId.BaseStatsIncrease);
	}

	default Percent getBaseStatsIncreasePct() {
		return getPercent(AttributeId.BaseStatsIncreasePct);
	}

	default Percent getStaIncreasePct() {
		return getPercent(AttributeId.staIncreasePct);
	}

	default Percent getIntIncreasePct() {
		return getPercent(AttributeId.intIncreasePct);
	}

	default Percent getSpiIncreasePct() {
		return getPercent(AttributeId.spiIncreasePct);
	}

	default double getSpellPower() {
		return getDouble(AttributeId.SpellPower);
	}

	default double getSpellDamage() {
		return getDouble(AttributeId.SpellDamage);
	}

	default double getSpellDamage(SpellSchool spellSchool) {
		return getDouble(AttributeId.SpellDamage, spellSchool);
	}

	default double getTotalSpellDamage() {
		return getSpellPower() + getSpellDamage();
	}

	default double getTotalSpellDamage(SpellSchool spellSchool) {
		return getSpellPower() + getSpellDamage(spellSchool);
	}

	default double getSpellCritRating() {
		return getDouble(AttributeId.SpellCritRating);
	}

	default Percent getSpellCritPct() {
		return getPercent(AttributeId.SpellCritPct);
	}

	default Percent getIncreasedCriticalDamagePct() {
		return getPercent(AttributeId.IncreasedCriticalDamagePct);
	}

	default Percent getCritDamageIncreasePct() {
		return getPercent(AttributeId.critDamageIncreasePct);
	}

	default double getSpellHitRating() {
		return getDouble(AttributeId.SpellHitRating);
	}

	default Percent getSpellHitPct() {
		return getPercent(AttributeId.SpellHitPct);
	}

	default double getSpellHasteRating() {
		return getDouble(AttributeId.SpellHasteRating);
	}

	default Percent getSpellHastePct() {
		return getPercent(AttributeId.SpellHastePct);
	}

	default double getSpellPenetration() {
		return getDouble(AttributeId.SpellPenetration);
	}

	default double getHealingPower() {
		return getDouble(AttributeId.HealingPower);
	}

	default double getMp5() {
		return getDouble(AttributeId.Mp5);
	}

	default double getResistance() {
		return getDouble(AttributeId.Resistance);
	}

	default double getResistance(SpellSchool spellSchool) {
		return getDouble(AttributeId.Resistance, spellSchool);
	}

	default Percent getDamageTakenPct() {
		return getPercent(AttributeId.DamageTakenPct);
	}

	default Percent getDirectDamageIncreasePct() {
		return getPercent(AttributeId.directDamageIncreasePct);
	}

	default Percent getDoTDamageIncreasePct() {
		return getPercent(AttributeId.dotDamageIncreasePct);
	}

	default Percent getAdditionalSpellDamageTakenPct() {
		return getPercent(AttributeId.AdditionalSpellDamageTakenPct);
	}

	default Percent getEffectIncreasePct() {
		return getPercent(AttributeId.effectIncreasePct);
	}

	default Percent getDamageTakenIncreasePct() {
		return getPercent(AttributeId.damageTakenIncreasePct);
	}

	default Percent getSpellCoeffPct() {
		return getPercent(AttributeId.spellCoeffBonusPct);
	}

	default Duration getCastTimeReduction() {
		return getDuration(AttributeId.castTimeReduction);
	}

	default Percent getCostReductionPct() {
		return getPercent(AttributeId.costReductionPct);
	}

	default List<SpecialAbility> getSpecialAbilities() {
		return getList(AttributeId.SpecialAbilities);
	}

	default double getExtraCritCoeff() {
		return getDouble(AttributeId.extraCritCoeff);
	}

	default boolean hasCasterStats() {
		return hasCasterStats(null);
	}

	default boolean hasCasterStats(SpellSchool spellSchool) {
		return (getTotalSpellDamage(spellSchool) != 0 || getSpellCritRating() != 0 || getSpellHitRating() != 0 || getSpellHasteRating() != 0) && getHealingPower() == 0;
	}

	default Percent getThreatReductionPct() {
		return getPercent(AttributeId.threatReductionPct);
	}

	default Percent getSpeedIncreasePct() {
		return getPercent(AttributeId.SpeedIncreasePct);
	}
}
