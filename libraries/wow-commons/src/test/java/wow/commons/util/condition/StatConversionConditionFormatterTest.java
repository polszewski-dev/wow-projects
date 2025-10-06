package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.util.condition.StatConversionConditionFormatter.formatCondition;
import static wow.commons.util.condition.StatConversionConditionParserTest.TEST_DATA;
import static wow.commons.util.condition.StatConversionConditionParserTest.TestData;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
class StatConversionConditionFormatterTest {
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