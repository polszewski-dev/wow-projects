package wow.commons.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.util.AttributesParserTest.TEST_DATA;
import static wow.commons.util.AttributesParserTest.TestData;

/**
 * User: POlszewski
 * Date: 2025-03-05
 */
class AttributesFormaterTest {
	@ParameterizedTest
	@MethodSource("testData")
	void format(TestData testData) {
		var result = AttributesFormater.format(testData.attributes());

		assertThat(result).isEqualTo(testData.string());
	}

	@ParameterizedTest
	@MethodSource("testData")
	void formatWithoutValue(TestData testData) {
		if (testData.attributes().list().size() > 1) {
			return;
		}

		var firstAttribute = testData.attributes().list().getFirst();
		var result = AttributesFormater.formatWithoutValue(firstAttribute);

		assertThat(result).isEqualTo(testData.stringWithoutValue());
	}

	static Stream<TestData> testData() {
		return TEST_DATA.stream();
	}
}