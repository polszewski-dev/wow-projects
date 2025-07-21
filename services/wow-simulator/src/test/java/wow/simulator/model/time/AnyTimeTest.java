package wow.simulator.model.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.model.time.Time.TIME_IN_INFINITY;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class AnyTimeTest {
	@ParameterizedTest
	@CsvSource({
			"10,  20,  30",
			"10,  INF, INF",
			"INF, 20,  INF",
			"INF, INF, INF",
	})
	void addAnyDuration(String firstStr, String secondStr, String expectedStr) {
		var first = parse(firstStr);
		var second = AnyDuration.parse(secondStr);
		var expected = parse(expectedStr);

		var result = first.add(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"10,  20,  30",
			"INF, 20,  INF",
	})
	void addDuration(String firstStr, String secondStr, String expectedStr) {
		var first = parse(firstStr);
		var second = Duration.parse(secondStr);
		var expected = parse(expectedStr);

		var result = first.add(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"10,  20,  false",
			"20,  10,  true",
			"10,  10,  false",
			"INF, 20,  true",
	})
	void after(String firstStr, String secondStr, boolean expected) {
		var first = parse(firstStr);
		var second = (Time) parse(secondStr);

		var result = first.after(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"10,  20,  true",
			"20,  10,  false",
			"10,  10,  false",
			"INF, 20,  false",
	})
	void before(String firstStr, String secondStr, boolean expected) {
		var first = parse(firstStr);
		var second = (Time) parse(secondStr);

		var result = first.before(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"10,  20,  -1",
			"20,  10,  1",
			"10,  10,  0",
			"10,  INF, -1",
			"INF, 10,  1",
			"INF, INF, 0",
	})
	void compareTo(String firstStr, String secondStr, int expected) {
		var first = parse(firstStr);
		var second = parse(secondStr);

		var result = first.compareTo(second);

		assertThat(result).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"1.25, 1.250",
			"10,   10.000",
			"INF,  INF",
	})
	void toString(String firstStr, String expected) {
		var first = parse(firstStr);

		var result = first.toString();

		assertThat(result).isEqualTo(expected);
	}

	static AnyTime parse(String value) {
		if (value.equals("INF")) {
			return TIME_IN_INFINITY;
		}
		return Time.at(Double.parseDouble(value));
	}
}