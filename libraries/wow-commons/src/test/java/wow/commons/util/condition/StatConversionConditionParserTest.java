package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.effect.component.StatConversionCondition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.effect.component.StatConversionCondition.EMPTY;
import static wow.commons.model.spell.ActionType.SPELL;
import static wow.commons.util.condition.StatConversionConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
class StatConversionConditionParserTest {
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
			testData(null, EMPTY),
			testData("", EMPTY),
			testData("Spell", StatConversionCondition.of(SPELL)),
			testData("SpellDamage", StatConversionCondition.of(SPELL_DAMAGE)),
			testData("Healing", StatConversionCondition.of(HEALING))
	);

	record TestData(String string, StatConversionCondition condition) {}

	static TestData testData(String string, StatConversionCondition condition) {
		return new TestData(string, condition);
	}
}