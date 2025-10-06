package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.effect.component.AbsorptionCondition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.PowerType.MELEE;
import static wow.commons.model.effect.component.AbsorptionCondition.EMPTY;
import static wow.commons.model.spell.ActionType.PHYSICAL;
import static wow.commons.model.spell.SpellSchool.*;
import static wow.commons.util.condition.AbsorptionConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
class AbsorptionConditionParserTest {
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
			testData("Arcane", AbsorptionCondition.of(ARCANE)),
			testData("Fire", AbsorptionCondition.of(FIRE)),
			testData("Frost", AbsorptionCondition.of(FROST)),
			testData("Melee", AbsorptionCondition.of(MELEE)),
			testData("Nature", AbsorptionCondition.of(NATURE)),
			testData("Physical", AbsorptionCondition.of(PHYSICAL)),
			testData("Shadow", AbsorptionCondition.of(SHADOW))
	);

	record TestData(String string, AbsorptionCondition condition) {}

	static TestData testData(String string, AbsorptionCondition condition) {
		return new TestData(string, condition);
	}
}