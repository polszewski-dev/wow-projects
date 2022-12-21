package wow.character.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.equipment.Equipment;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttributeId;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.SpellSchool.FROST;
import static wow.commons.model.spells.SpellSchool.SHADOW;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
class AttributeEvaluatorTest extends WowCharacterSpringTest {
	@Test
	@DisplayName("nothingToSolve returns empty attributes if none were added")
	void empty() {
		Attributes attributes = AttributeEvaluator.of().nothingToSolve();

		assertThat(attributes.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("nothingToSolve sums item attributes, leaves special abilities/sockets/set pieces")
	void nothingToSolveSumsElementsAndLeavesSpecialAbilities() {
		Equipment equipment = getEquipment();

		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(equipment)
				.nothingToSolve();

		assertThat(attributes.getSpecialAbilities()).hasSize(2);
		assertThat(attributes.getList(ComplexAttributeId.SOCKETS)).hasSize(10);
		assertThat(attributes.getList(ComplexAttributeId.SET_PIECES)).hasSize(4);
	}

	@Test
	@DisplayName("solveAllLeaveAbilities sums item attributes/sockets/set pieces, leaves special abilities")
	void solveAllLeaveAbilitiesSumsElementsAndLeavesSpecialAbilities() {
		Equipment equipment = getEquipment();

		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(equipment)
				.solveAllLeaveAbilities();

		assertThat(attributes.getSpecialAbilities()).hasSize(4);
		assertThat(attributes.getList(ComplexAttributeId.SOCKETS)).isEmpty();
		assertThat(attributes.getList(ComplexAttributeId.SET_PIECES)).isEmpty();

		assertThat(attributes.getStamina()).isEqualTo(462);
		assertThat(attributes.getIntellect()).isEqualTo(373);
		assertThat(attributes.getBaseStatsIncrease()).isEqualTo(6);
		assertThat(attributes.getSpellPower()).isEqualTo(1170);
		assertThat(attributes.getSpellDamage()).isEqualTo(156);
		assertThat(attributes.getSpellDamage(SHADOW)).isEqualTo(54);
		assertThat(attributes.getSpellDamage(FROST)).isEqualTo(54);
		assertThat(attributes.getSpellHitRating()).isEqualTo(164);
		assertThat(attributes.getSpellCritRating()).isEqualTo(317);
		assertThat(attributes.getSpellHasteRating()).isEqualTo(426);
		assertThat(attributes.getIncreasedCriticalDamagePct().getValue()).isEqualTo(3);
	}
}