package wow.commons.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
class AnyDurationTest {
	@ParameterizedTest
	@CsvSource({
			"0, 0",
			"2, 2",
			"2.0, 2",
			"2.5, 2.5",
			"15, 15",
			"25ms, 0.025",
			"25s, 25",
			"25m, 1500",
			"25h, 90000",
	})
	void parse(String firstStr, double expected) {
		var first = (Duration) AnyDuration.parse(firstStr);

		var result = first.getSeconds();

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void parseNull() {
		assertThat(AnyDuration.parse(null)).isNull();
	}

	@Test
	void parseThrowsException() {
		assertThatThrownBy(() -> AnyDuration.parse("-10")).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({
			"2,   3,   -1",
			"3,   2,   1",
			"2,   2,   0",
			"2,   INF, -1",
			"INF, 2,   1",
			"INF, INF, 0",
	})
	void compareTo(String firstStr, String secondStr, int expected) {
		var first = AnyDuration.parse(firstStr);
		var second = AnyDuration.parse(secondStr);

		var result = first.compareTo(second);

		assertThat(result).isEqualTo(expected);
	}
}