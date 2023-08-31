package wow.commons.model.spell;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
class TickSchemeTest {
	@Test
	void defaultWeights() {
		TickScheme scheme = TickScheme.DEFAULT;

		assertThat(scheme.tickWeights()).isEqualTo(List.of(1.0));
		assertThat(scheme.weight(1)).isEqualTo(1);
		assertThat(scheme.weight(2)).isEqualTo(1);
		assertThat(scheme.weight(3)).isEqualTo(1);
		assertThat(scheme.weight(4)).isEqualTo(1);
		assertThat(scheme.weight(5)).isEqualTo(1);
		assertThat(scheme.weight(6)).isEqualTo(1);
	}

	@Test
	void customWeights() {
		TickScheme scheme = new TickScheme(List.of(0.5, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.5, 1.5, 1.5, 1.5));

		assertThat(scheme.tickWeights()).isEqualTo(List.of(0.5, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.5, 1.5, 1.5, 1.5));
		assertThat(scheme.weight(1)).isEqualTo(0.5);
		assertThat(scheme.weight(2)).isEqualTo(0.5);
		assertThat(scheme.weight(3)).isEqualTo(0.5);
		assertThat(scheme.weight(4)).isEqualTo(0.5);
		assertThat(scheme.weight(5)).isEqualTo(1);
		assertThat(scheme.weight(6)).isEqualTo(1);
		assertThat(scheme.weight(7)).isEqualTo(1);
		assertThat(scheme.weight(8)).isEqualTo(1);
		assertThat(scheme.weight(9)).isEqualTo(1.5);
		assertThat(scheme.weight(10)).isEqualTo(1.5);
		assertThat(scheme.weight(11)).isEqualTo(1.5);
		assertThat(scheme.weight(12)).isEqualTo(1.5);
		assertThat(scheme.weight(13)).isEqualTo(1.5);
		assertThat(scheme.weight(14)).isEqualTo(1.5);
		assertThat(scheme.weight(15)).isEqualTo(1.5);
	}
}