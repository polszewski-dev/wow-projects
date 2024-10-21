package wow.commons.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * User: POlszewski
 * Date: 2022-09-26
 */
class PercentTest {
	@ParameterizedTest
	@CsvSource({
			"0%, 0",
			"1%, 1",
			"100%, 100",
			"1.25%, 1.25",
			"1.25%, 1.247",
	})
	void testToString(String expected, double value) {
		assertEquals(expected, Percent.of(value).toString());
	}
}