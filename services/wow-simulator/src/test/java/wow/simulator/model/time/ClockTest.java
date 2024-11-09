package wow.simulator.model.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class ClockTest {
	@Test
	void advanceTo() {
		assertThat(clock.now()).isEqualTo(Time.at(10));

		Time inThePast = Time.at(5);

		assertThatThrownBy(() -> clock.advanceTo(inThePast)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void after() {
		assertThat(clock.after(Duration.seconds(10))).isEqualTo(Time.at(20));
	}

	@Test
	void afterSeconds() {
		assertThat(clock.afterSeconds(10)).isEqualTo(Time.at(20));
	}

	@Test
	void timeInTheFuture() {
		assertThat(clock.timeInTheFuture(Time.at(5))).isFalse();
		assertThat(clock.timeInTheFuture(Time.at(10))).isFalse();
		assertThat(clock.timeInTheFuture(Time.at(15))).isTrue();
	}

	@Test
	void timeInTheFutureOrPresent() {
		assertThat(clock.timeInTheFutureOrPresent(Time.at(5))).isFalse();
		assertThat(clock.timeInTheFutureOrPresent(Time.at(10))).isTrue();
		assertThat(clock.timeInTheFutureOrPresent(Time.at(15))).isTrue();
	}

	@Test
	void timeInThePast() {
		assertThat(clock.timeInThePast(Time.at(5))).isTrue();
		assertThat(clock.timeInThePast(Time.at(10))).isFalse();
		assertThat(clock.timeInThePast(Time.at(15))).isFalse();
	}

	@Test
	void timeInThePastOrPresent() {
		assertThat(clock.timeInThePastOrPresent(Time.at(5))).isTrue();
		assertThat(clock.timeInThePastOrPresent(Time.at(10))).isTrue();
		assertThat(clock.timeInThePastOrPresent(Time.at(15))).isFalse();
	}

	@Test
	void timeInThePresent() {
		assertThat(clock.timeInThePresent(Time.at(5))).isFalse();
		assertThat(clock.timeInThePresent(Time.at(10))).isTrue();
		assertThat(clock.timeInThePresent(Time.at(15))).isFalse();
	}

	Clock clock;

	@BeforeEach
	void setUp() {
		clock = new Clock();
		clock.advanceTo(Time.at(10));
	}
}