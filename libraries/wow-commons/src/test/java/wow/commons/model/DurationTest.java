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
class DurationTest {
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
		var first = Duration.parse(firstStr);

		var result = first.getSeconds();

		assertThat(result).isEqualTo(expected);
	}

	@Test
	void parseNull() {
		assertThat(Duration.parse(null)).isNull();
	}

	@Test
	void parseThrowsException() {
		assertThatThrownBy(() -> Duration.parse("-10")).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({
			"2,   3,   5",
	})
	void add(String firstStr, String secondStr, String expectedStr) {
		var first = Duration.parse(firstStr);
		var second = Duration.parse(secondStr);
		var expected = Duration.parse(expectedStr);

		var result = first.add(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"5, 2,   2.5",
			"0, 2,   0",
	})
	void divideByDuration(String firstStr, String secondStr, double expected) {
		var first = Duration.parse(firstStr);
		var second = Duration.parse(secondStr);

		var result = first.divideBy(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"5,   0",
	})
	void divideByDurationThrowsException(String firstStr, String secondStr) {
		var first = Duration.parse(firstStr);
		var second = Duration.parse(secondStr);

		assertThatThrownBy(() -> first.divideBy(second)).isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({
			"2,   3,   2",
			"3,   2,   2",
	})
	void min(String firstStr, String secondStr, String expectedStr) {
		var first = Duration.parse(firstStr);
		var second = Duration.parse(secondStr);
		var expected = Duration.parse(expectedStr);

		var result = first.min(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"2,   3,   3",
			"3,   2,   3",
	})
	void max(String firstStr, String secondStr, String expectedStr) {
		var first = Duration.parse(firstStr);
		var second = Duration.parse(secondStr);
		var expected = Duration.parse(expectedStr);

		var result = first.max(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"2,   3,   -1",
			"3,   2,   1",
			"2,   2,   0",
	})
	void compareTo(String firstStr, String secondStr, int expected) {
		var first = Duration.parse(firstStr);
		var second = Duration.parse(secondStr);

		var result = first.compareTo(second);

		assertThat(result).isEqualTo(expected);
	}
}