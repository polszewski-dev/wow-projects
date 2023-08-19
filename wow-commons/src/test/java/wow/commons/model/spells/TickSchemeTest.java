package wow.commons.model.spells;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
class TickSchemeTest {
	@Test
	void defaultWeights() {
		TickScheme scheme = TickScheme.create(6, Duration.seconds(3));

		assertThat(scheme.numTicks()).isEqualTo(6);
		assertThat(scheme.tickInterval()).isEqualTo(Duration.seconds(3));
		assertThat(scheme.duration()).isEqualTo(Duration.seconds(18));
		assertThat(scheme.tickWeights()).isEqualTo(List.of(1, 1, 1, 1, 1, 1));
		assertThat(scheme.weight(0)).isEqualTo(1);
		assertThat(scheme.weight(5)).isEqualTo(1);
		assertThat(scheme.weightSum()).isEqualTo(6);
		assertThat(scheme.scale(600, 0)).isEqualTo(100);
		assertThat(scheme.scale(600, 5)).isEqualTo(100);
	}

	@Test
	void customWeights() {
		TickScheme scheme = TickScheme.create(List.of(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3), Duration.seconds(2));

		assertThat(scheme.numTicks()).isEqualTo(12);
		assertThat(scheme.tickInterval()).isEqualTo(Duration.seconds(2));
		assertThat(scheme.duration()).isEqualTo(Duration.seconds(24));
		assertThat(scheme.tickWeights()).isEqualTo(List.of(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3));
		assertThat(scheme.weight(0)).isEqualTo(1);
		assertThat(scheme.weight(11)).isEqualTo(3);
		assertThat(scheme.weightSum()).isEqualTo(24);
		assertThat(scheme.scale(600, 0)).isEqualTo(25);
		assertThat(scheme.scale(600, 4)).isEqualTo(50);
		assertThat(scheme.scale(600, 8)).isEqualTo(75);
		assertThat(scheme.scale(600, 11)).isEqualTo(75);
	}

	@Test
	void adjustBaseDuration2Added() {
		TickScheme scheme = TickScheme.create(List.of(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3), Duration.seconds(2));

		scheme = scheme.adjustBaseDuration(Duration.seconds(28));

		assertThat(scheme.numTicks()).isEqualTo(14);
		assertThat(scheme.tickInterval()).isEqualTo(Duration.seconds(2));
		assertThat(scheme.duration()).isEqualTo(Duration.seconds(28));
		assertThat(scheme.tickWeights()).isEqualTo(List.of(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3));
		assertThat(scheme.weight(0)).isEqualTo(1);
		assertThat(scheme.weight(13)).isEqualTo(3);
		assertThat(scheme.weightSum()).isEqualTo(30);
		assertThat(scheme.scale(600, 0)).isEqualTo(20);
		assertThat(scheme.scale(600, 4)).isEqualTo(40);
		assertThat(scheme.scale(600, 8)).isEqualTo(60);
		assertThat(scheme.scale(600, 13)).isEqualTo(60);
	}

	@Test
	void adjustBaseDuration2Removed() {
		TickScheme scheme = TickScheme.create(List.of(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3), Duration.seconds(2));

		scheme = scheme.adjustBaseDuration(Duration.seconds(20));

		assertThat(scheme.numTicks()).isEqualTo(10);
		assertThat(scheme.tickInterval()).isEqualTo(Duration.seconds(2));
		assertThat(scheme.duration()).isEqualTo(Duration.seconds(20));
		assertThat(scheme.tickWeights()).isEqualTo(List.of(1, 1, 1, 1, 2, 2, 2, 2, 3, 3));
		assertThat(scheme.weight(0)).isEqualTo(1);
		assertThat(scheme.weight(9)).isEqualTo(3);
		assertThat(scheme.weightSum()).isEqualTo(18);
		assertThat(scheme.scale(600, 0)).isEqualTo(33);
		assertThat(scheme.scale(600, 4)).isEqualTo(66);
		assertThat(scheme.scale(600, 8)).isEqualTo(100);
		assertThat(scheme.scale(600, 9)).isEqualTo(100);
	}
}