package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.spell.SpellTargetCondition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.constant.AbilityIds.IMMOLATE;
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
					"HasEffect:Immolate",
					new HasEffect(IMMOLATE)
			),
			testData(
					"HealthAtMost20%",
					new HealthAtMostPct(20)
			)
	);

	record TestData(String string, SpellTargetCondition condition) {}

	static TestData testData(String string, SpellTargetCondition condition) {
		return new TestData(string, condition);
	}
}