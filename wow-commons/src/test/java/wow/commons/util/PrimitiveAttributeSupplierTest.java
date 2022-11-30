package wow.commons.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.SPELL_POWER;
import static wow.commons.model.character.CreatureType.DEMON;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.character.PetType.IMP;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;
import static wow.commons.model.spells.SpellSchool.*;
import static wow.commons.model.talents.TalentTree.DESTRUCTION;

/**
 * User: POlszewski
 * Date: 2022-11-10
 */
class PrimitiveAttributeSupplierTest {
	@Test
	@DisplayName("Providing zero gives empty list")
	void zeroShouldGenerateEmptyList() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower");
		Attributes attributes = supplier.getAttributes(0);

		assertThat(attributes.getPrimitiveAttributeList()).isEmpty();
	}

	@Test
	@DisplayName("Providing no condition produces unconditional attribute")
	void noConditionProducesUnconditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(1);

		assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(Attribute.of(SPELL_POWER, 10));
	}

	@Test
	@DisplayName("Providing TalentTree produces conditional attribute")
	void talentTreeProducesConditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Destruction");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(1);

		assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
				Attribute.of(SPELL_POWER, 10, AttributeCondition.of(DESTRUCTION)));
	}

	@Test
	@DisplayName("Providing SpellSchool produces conditional attribute")
	void spellSchoolProducesConditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Fire");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(1);

		assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
				Attribute.of(SPELL_POWER, 10, AttributeCondition.of(FIRE)));
	}

	@Test
	@DisplayName("Providing SpellId produces conditional attribute")
	void spellIdProducesConditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Shadow Bolt");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(1);

		assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
				Attribute.of(SPELL_POWER, 10, AttributeCondition.of(SHADOW_BOLT)));
	}

	@Test
	@DisplayName("Providing PetType produces conditional attribute")
	void petTypeProducesConditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Imp");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(1);

		assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
				Attribute.of(SPELL_POWER, 10, AttributeCondition.of(IMP)));
	}

	@Test
	@DisplayName("Providing CreatureType produces conditional attribute")
	void creatureTypeProducesConditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Undead");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(1);

		assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
				Attribute.of(SPELL_POWER, 10, AttributeCondition.of(UNDEAD)));
	}

	@Test
	@DisplayName("Providing multiple conditions of the same type produce multiple conditional attributes")
	void multipleConditionsOfTheSameTypeProduceConditionalAttributes() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Fire,Frost,Arcane");
		Attributes attributes = supplier.getAttributes(10);


		assertThat(attributes.getPrimitiveAttributeList()).hasSize(3);

		assertAll(
				() -> assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(FIRE))),
				() -> assertThat(attributes.getPrimitiveAttributeList().get(1)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(FROST))),
				() -> assertThat(attributes.getPrimitiveAttributeList().get(2)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(ARCANE)))
		);
	}

	@Test
	@DisplayName("Providing single conditions of different type produces multiple conditional attributes")
	void singleConditionsOfDifferentTypeProduceSingleConditionalAttribute() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Fire,Undead");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(2);

		assertAll(
				() -> assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(FIRE))),
				() -> assertThat(attributes.getPrimitiveAttributeList().get(1)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(UNDEAD)))
		);
	}

	@Test
	@DisplayName("Providing multiple conditions of different types produces multiple conditional attributes")
	void multipleConditionsOfDifferentTypesProduceMultipleConditionalAttributes() {
		PrimitiveAttributeSupplier supplier = PrimitiveAttributeSupplier.fromString("SpellPower,Fire,Frost,Undead,Demon");
		Attributes attributes = supplier.getAttributes(10);

		assertThat(attributes.getPrimitiveAttributeList()).hasSize(4);

		assertAll(
				() -> assertThat(attributes.getPrimitiveAttributeList().get(0)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(FIRE))),

				() -> assertThat(attributes.getPrimitiveAttributeList().get(1)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(FROST))),

				() -> assertThat(attributes.getPrimitiveAttributeList().get(2)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(UNDEAD))),

				() -> assertThat(attributes.getPrimitiveAttributeList().get(3)).isEqualTo(
						Attribute.of(SPELL_POWER, 10, AttributeCondition.of(DEMON)))
		);
	}
}