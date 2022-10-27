package wow.commons.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
class ParserUtilTest {
	@ParameterizedTest
	@CsvSource({
			"$2,B",
			"$1-$1,A-A",
			"$1m,Am",
			"k$2m,kBm",
	})
	void substituteParams(String pattern, String expected) {
		String result = ParserUtil.substituteParams(pattern, p -> "" + "ABCDE".charAt(p - 1));
		assertThat(result).isEqualTo(expected);
	}
}