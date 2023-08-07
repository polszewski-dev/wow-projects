package wow.simulator.model.time;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class TimeTest {
	@Test
	void at() {
		Time time = Time.at(10);

		assertThat(time.timestamp()).isEqualTo(10_000);

		assertThatThrownBy(() -> Time.at(-10)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void secondsSinceZero() {
		Time time = Time.at(1.25);

		assertThat(time.secondsSinceZero()).isEqualTo(1.25);
	}

	@Test
	void isAtZero() {
		assertThat(Time.at(0).isAtZero()).isTrue();
		assertThat(Time.at(10).isAtZero()).isFalse();
	}

	@Test
	void isInInfinity() {
		assertThat(Time.at(0).isInInfinity()).isFalse();
		assertThat(Time.INFINITY.isInInfinity()).isTrue();
	}

	@Test
	void add() {
		Time time = Time.at(10);

		assertThat(time.add(Duration.seconds(20))).isEqualTo(Time.at(30));
		assertThat(time.add(Duration.seconds(-5))).isEqualTo(Time.at(5));

		assertThat(time.add(Duration.INFINITE)).isEqualTo(Time.INFINITY);
		assertThat(Time.INFINITY.add(Duration.seconds(20))).isEqualTo(Time.INFINITY);
		assertThat(Time.INFINITY.add(Duration.INFINITE)).isEqualTo(Time.INFINITY);

		Duration duration = Duration.seconds(-20);
		assertThatThrownBy(() -> time.add(duration)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void subtract() {
		Time time = Time.at(10);

		assertThat(time.subtract(Duration.seconds(10))).isEqualTo(Time.at(0));
		assertThat(time.subtract(Duration.seconds(5))).isEqualTo(Time.at(5));
		assertThat(time.subtract(Duration.seconds(0))).isEqualTo(Time.at(10));
		assertThat(time.subtract(Duration.seconds(-5))).isEqualTo(Time.at(15));

		Duration duration = Duration.seconds(15);

		assertThatThrownBy(() -> time.subtract(duration)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> time.subtract(Duration.INFINITE)).isInstanceOf(IllegalArgumentException.class);
		assertThat(Time.INFINITY.subtract(duration)).isEqualTo(Time.INFINITY);
		assertThatThrownBy(() -> Time.INFINITY.subtract(Duration.INFINITE)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void subtractTime() {
		Time time1 = Time.at(10);
		Time time2 = Time.at(20);

		assertThat(time1.subtract(time2)).isEqualTo(Duration.seconds(-10));
		assertThat(time1.subtract(time1)).isEqualTo(Duration.seconds(0));
		assertThat(time2.subtract(time1)).isEqualTo(Duration.seconds(10));

		assertThat(Time.INFINITY.subtract(time1)).isEqualTo(Duration.INFINITE);
		assertThatThrownBy(() -> time1.subtract(Time.INFINITY)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> Time.INFINITY.subtract(Time.INFINITY)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void min() {
		Time time1 = Time.at(1);
		Time time2 = Time.at(2);

		assertThat(time1.min(time2)).isEqualTo(time1);
		assertThat(time2.min(time1)).isEqualTo(time1);
	}

	@Test
	void max() {
		Time time1 = Time.at(1);
		Time time2 = Time.at(2);

		assertThat(time1.max(time2)).isEqualTo(time2);
		assertThat(time2.max(time1)).isEqualTo(time2);
	}

	@Test
	void compareTo() {
		Time time1 = Time.at(1);
		Time time2 = Time.at(2);

		assertThat(time1).isLessThan(time2);
		assertThat(time2).isGreaterThan(time1);
		assertThat(time1).isEqualByComparingTo(time1);
	}

	@Test
	void testToString() {
		assertThat(Time.at(1.25)).hasToString("1.250");
		assertThat(Time.INFINITY).hasToString("INF");
	}
}