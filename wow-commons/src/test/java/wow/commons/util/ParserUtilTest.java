package wow.commons.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
class ParserUtilTest {
	@Test
	void parseMultipleValues() {
		Object[] result = ParserUtil.parseMultipleValues("BossDrop:(.*):(.*)", "BossDrop:ABC:123");

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result[0]).isEqualTo("ABC");
		assertThat(result[1]).isEqualTo(123);
	}

	@Test
	void parseMultipleInts() {
		int[] result = ParserUtil.parseMultipleInts("(\\d+) Min, (\\d+) Sec", "2 Min, 30 Sec");

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result[0]).isEqualTo(2);
		assertThat(result[1]).isEqualTo(30);
	}

	@Test
	void removePrefix() {
		String result = ParserUtil.removePrefix("Crafted:", "Crafted:ABC");

		assertThat(result).isEqualTo("ABC");
	}

	@DisplayName("Placeholders are substituted correctly")
	@ParameterizedTest(name = "[{index}] PATTERN = {0}, RESULT = {1}")
	@CsvSource({
			"$2,B",
			"$1-$1,A-A",
			"$1m,Am",
			"k$2m,kBm",
			"nothing,nothing"
	})
	void substituteParams(String pattern, String expected) {
		String result = ParserUtil.substituteParams(pattern, p -> "" + "ABCDE".charAt(p - 1));
		assertThat(result).isEqualTo(expected);
	}
}