package wow.commons.util.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.util.condition.EventConditionFormatter.formatCondition;
import static wow.commons.util.condition.EventConditionParserTest.TEST_DATA;
import static wow.commons.util.condition.EventConditionParserTest.TestData;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
class EventConditionFormatterTest {
	@ParameterizedTest
	@MethodSource("getTestData")
	void format(TestData data) {
		var formatted = formatCondition(data.condition());

		assertThat(formatted).isEqualTo(data.string());
	}

	static List<TestData> getTestData() {
		var excludedStrings = Arrays.asList(
				null,
				"(Create Firestone (Greater))",
				"((Create Firestone (Greater)))"
		);

		return TEST_DATA.stream()
				.filter(data -> !excludedStrings.contains(data.string()))
				.toList();
	}
}