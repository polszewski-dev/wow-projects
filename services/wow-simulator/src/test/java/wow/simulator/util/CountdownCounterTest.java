package wow.simulator.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * User: POlszewski
 * Date: 2026-08-12
 */
class CountdownCounterTest {
	int step = 0;
	int capture;

	@Test
	void decreasing_to_zero_runs_provided_action() {
		var initialValue = 3;
		var counter = new CountdownCounter(
				initialValue,
				() -> capture = step
		);

		for (int i = 0; i < 3; ++i) {
			++step;
			counter.decrease();
		}

		assertThat(capture).isEqualTo(3);
	}

	@Test
	void decreasing_below_zero_throws_exception() {
		var counter = new CountdownCounter(
				3, () -> {}
		);

		counter.decrease();
		counter.decrease();
		counter.decrease();

		assertThatThrownBy(counter::decrease).isInstanceOf(IllegalStateException.class);
	}
}