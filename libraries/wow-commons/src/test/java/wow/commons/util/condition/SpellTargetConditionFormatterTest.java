package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.util.condition.SpellTargetConditionFormatter.formatCondition;
import static wow.commons.util.condition.SpellTargetConditionParserTest.TEST_DATA;
import static wow.commons.util.condition.SpellTargetConditionParserTest.TestData;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
class SpellTargetConditionFormatterTest {
	@ParameterizedTest
	@MethodSource("getTestData")
	void format(TestData data) {
		var formatted = formatCondition(data.condition());

		assertThat(formatted).isEqualTo(data.string());
	}

	static List<TestData> getTestData() {
		return TEST_DATA.stream()
				.filter(data -> data.string() != null)
				.toList();
	}
}