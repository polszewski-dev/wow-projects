package wow.commons.model.attributes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.commons.model.attributes.condition.AttributeCondition;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.spells.SpellSchool.FIRE;
import static wow.commons.model.spells.SpellSchool.SHADOW;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
class AttributeSourceTest {
	@Test
	@DisplayName("Multiple attributes of the same id and no condition sum correctly")
	void sameIdNoCondition() {
		Attributes attributes = Attributes.of(
				Attribute.of(SPELL_DAMAGE, 10),
				Attribute.of(SPELL_DAMAGE, 20),
				Attribute.of(SPELL_DAMAGE, 30)
		);

		assertThat(attributes.getSpellDamage()).isEqualTo(60);
	}

	@Test
	@DisplayName("Multiple attributes of the same id and same condition sum correctly")
	void sameIdSameCondition() {
		Attributes attributes = Attributes.of(
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(FIRE)),
				Attribute.of(SPELL_DAMAGE, 20, AttributeCondition.of(FIRE)),
				Attribute.of(SPELL_DAMAGE, 30, AttributeCondition.of(FIRE))
		);

		assertThat(attributes.getSpellDamage()).isZero();
		assertThat(attributes.getSpellDamage(FIRE)).isEqualTo(60);
	}

	@Test
	@DisplayName("Multiple attributes of the same id and different conditions sum correctly")
	void sameIdDifferentCondition() {
		Attributes attributes = Attributes.of(
				Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(FIRE)),
				Attribute.of(SPELL_DAMAGE, 20, AttributeCondition.of(SHADOW)),
				Attribute.of(SPELL_DAMAGE, 30, AttributeCondition.of(FIRE)),
				Attribute.of(SPELL_DAMAGE, 40),
				Attribute.of(SPELL_DAMAGE, 50, AttributeCondition.of(SHADOW)),
				Attribute.of(SPELL_DAMAGE, 60)
		);

		assertThat(attributes.getSpellDamage()).isEqualTo(100);
		assertThat(attributes.getSpellDamage(FIRE)).isEqualTo(40);
		assertThat(attributes.getSpellDamage(SHADOW)).isEqualTo(70);
	}

	@Test
	@DisplayName("Multiple attributes of different id and no condition")
	void differentIdNoCondition() {
		Attributes attributes = Attributes.of(
				Attribute.of(SPELL_DAMAGE, 10),
				Attribute.of(SPELL_HASTE_RATING, 20),
				Attribute.of(SPELL_CRIT_RATING, 30),
				Attribute.of(SPELL_DAMAGE, 40)
		);

		assertThat(attributes.getSpellDamage()).isEqualTo(50);
		assertThat(attributes.getSpellHasteRating()).isEqualTo(20);
		assertThat(attributes.getSpellCritRating()).isEqualTo(30);
	}

}