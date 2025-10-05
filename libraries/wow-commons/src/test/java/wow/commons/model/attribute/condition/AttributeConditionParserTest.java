package wow.commons.model.attribute.condition;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.*;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.*;
import static wow.commons.model.attribute.condition.AttributeConditionFormatter.formatCondition;
import static wow.commons.model.attribute.condition.AttributeConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
class AttributeConditionParserTest {
	@Test
	void emptyCondition() {
		assertThat(parseCondition(null)).isEqualTo(AttributeCondition.EMPTY);
		assertThat(parseCondition("")).isEqualTo(AttributeCondition.EMPTY);
		assertThat(parseCondition("   ")).isEqualTo(AttributeCondition.EMPTY);
	}

	@Test
	void orExpression() {
		var parsed = parseCondition("Shadow Bolt | Incinerate");
		var expected = ConditionOperator.or(
				AttributeCondition.of(SHADOW_BOLT),
				AttributeCondition.of(INCINERATE)
		);

		assertThat(parsed).isEqualTo(expected);
	}

	@Test
	void andExpression() {
		var parsed = parseCondition("Shadow Bolt & Incinerate");
		var expected = ConditionOperator.and(
				AttributeCondition.of(SHADOW_BOLT),
				AttributeCondition.of(INCINERATE)
		);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"Create Firestone (Greater)",
			"(Create Firestone (Greater))",
			"((Create Firestone (Greater)))"
	})
	void paren(String value) {
		var parsed = parseCondition(value);
		var expected = AttributeCondition.of(CREATE_FIRESTONE_GREATER);

		assertThat(parsed).isEqualTo(expected);
	}

	@Test
	void commaExpression() {
		var parsed = parseCondition("Shadow Bolt, Incinerate");
		var expected = ConditionOperator.comma(
				AttributeCondition.of(SHADOW_BOLT),
				AttributeCondition.of(INCINERATE)
		);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(TalentTree.class)
	void talentTree(TalentTree value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(SpellSchool.class)
	void spellSchool(SpellSchool value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getAbilityIds")
	void abilityId(AbilityId value) {
		if (value.equals(ATIESH_GREATSTAFF_OF_THE_GUARDIAN)) {// comma in the name :/
			return;
		}

		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	static Collection<AbilityId> getAbilityIds() {
		return AbilityId.values();
	}

	@ParameterizedTest
	@EnumSource(AbilityCategory.class)
	void abilityCategory(AbilityCategory value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(PetType.class)
	void abilityId(PetType value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(CreatureType.class)
	void targetType(CreatureType value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(DruidFormType.class)
	void druidFormType(DruidFormType value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(WeaponSubType.class)
	void weaponSubType(WeaponSubType value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(ProfessionId.class)
	void professionId(ProfessionId value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(MiscCondition.class)
	void miscCondition(MiscCondition value) {
		var parsed = parseCondition(value.toString());

		assertThat(parsed).isEqualTo(value);
	}

	@ParameterizedTest
	@EnumSource(EffectCategory.class)
	void effecType(EffectCategory value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@EnumSource(MovementType.class)
	void professionId(MovementType value) {
		var parsed = parseCondition(value.toString());
		var expected = AttributeCondition.of(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@Test
	void ownerisChanneling() {
		var expected = new OwnerIsChannelingCondition(DRAIN_SOUL);
		var value = formatCondition(expected);
		var parsed = parseCondition(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@ParameterizedTest
	@MethodSource("getAbilityIds")
	void ownerHasEffect(AbilityId abilityId) {
		if (abilityId.equals(ATIESH_GREATSTAFF_OF_THE_GUARDIAN)) {
			return;
		}

		var expected = new OwnerHasEffectCondition(abilityId);
		var value = formatCondition(expected);
		var parsed = parseCondition(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@Test
	void targetClass() {
		var expected = new TargetClassCondition(CharacterClassId.WARLOCK);
		var value = formatCondition(expected);
		var parsed = parseCondition(value);

		assertThat(parsed).isEqualTo(expected);
	}

	@Test
	void noDuplicatedKeys() {
		var keyFrequency = getAllKeys()
				.flatMap(Stream::of)
				.map(Object::toString)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		for (var entry : keyFrequency.entrySet()) {
			assertThat(entry.getValue())
					.withFailMessage(entry.getKey() + " has more than 1 occurence")
					.isEqualTo(1);
		}
	}

	private static Stream<Object[]> getAllKeys() {
		return Stream.of(
				AbilityCategory.values(),
				CreatureType.values(),
				DruidFormType.values(),
				EffectCategory.values(),
				MiscCondition.values(),
				MovementType.values(),
				PetType.values(),
				ProfessionId.values(),
				AbilityId.values().toArray(),
				SpellSchool.values(),
				TalentTree.values(),
				WeaponSubType.values()
		);
	}
}