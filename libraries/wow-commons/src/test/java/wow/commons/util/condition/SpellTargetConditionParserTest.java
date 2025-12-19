package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.character.DruidFormType;
import wow.commons.model.spell.SpellTargetCondition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.IMMOLATE;
import static wow.commons.model.character.CharacterClassId.MAGE;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.CreatureType.DEMON;
import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.commons.model.spell.SpellTargetCondition.*;
import static wow.commons.util.condition.SpellTargetConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
class SpellTargetConditionParserTest {
	@ParameterizedTest
	@MethodSource("getTestData")
	void testValues(TestData data) {
		var parsed = parseCondition(data.string);

		assertThat(parsed).isEqualTo(data.condition);
	}

	static List<TestData> getTestData() {
		return TEST_DATA;
	}

	static final List<TestData> TEST_DATA = List.of(
			testData(
					null,
					EMPTY
			),
			testData(
					"",
					EMPTY
			),
			testData(
					"Undead",
					of(UNDEAD)
			),
			testData(
					"HasEffect(Immolate)",
					new HasEffect(IMMOLATE.name())
			),
			testData(
					"Health% < 20",
					new HealthPctLessThan(20)
			),
			testData(
					"Health% <= 20",
					new HealthPctLessThanOrEqual(20)
			),
			testData(
					"Health% > 20",
					new HealthPctGreaterThan(20)
			),
			testData(
					"Health% >= 20",
					new HealthPctGreaterThanOrEqual(20)
			),
			testData(
					"Class = Warlock",
					new IsClass(WARLOCK)
			),
			testData(
					"CatForm",
					of(DruidFormType.CAT_FORM)
			),
			testData(
					"Friendly",
					FRIENDLY
			),
			testData(
					"Hostile",
					HOSTILE
			),
			testData(
					"Undead, Demon",
					comma(of(UNDEAD), of(DEMON))
			),
			testData(
					"Health% >= 40 & Health% < 70",
					and(
							new HealthPctGreaterThanOrEqual(40),
							new HealthPctLessThan(70)
					)
			),
			testData(
					"Class = Mage | Class = Warlock",
					or(new IsClass(MAGE), new IsClass(WARLOCK))
			),
			testData(
					"~HasEffect(Weakened Soul)",
					not(new HasEffect("Weakened Soul"))
			),
			testData(
					"~(Class = Mage | Class = Warlock)",
					not(or(new IsClass(MAGE), new IsClass(WARLOCK)))
			)
	);

	record TestData(String string, SpellTargetCondition condition) {}

	static TestData testData(String string, SpellTargetCondition condition) {
		return new TestData(string, condition);
	}
}