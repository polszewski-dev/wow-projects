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
	@MethodSource
	void test(TestData testData) {
		var result = AttributesFormater.format(testData.attributes());

		assertThat(result).isEqualTo(testData.string());
	}

	static Stream<TestData> test() {
		return TEST_DATA.stream();
	}
}