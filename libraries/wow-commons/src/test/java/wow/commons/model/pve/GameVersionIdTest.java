package wow.commons.model.pve;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.GameVersionId.*;
import static wow.commons.model.pve.PhaseId.*;

/**
 * User: POlszewski
 * Date: 2023-08-04
 */
class GameVersionIdTest {
	@Test
	void getEarliestPhase() {
		assertThat(VANILLA.getEarliestPhase()).isEqualTo(VANILLA_P1);
		assertThat(TBC.getEarliestPhase()).isEqualTo(TBC_P0);
		assertThat(WOTLK.getEarliestPhase()).isEqualTo(WOTLK_P0);
	}

	@Test
	void getEarliestNonPrepatchPhase() {
		assertThat(VANILLA.getEarliestNonPrepatchPhase()).isEqualTo(VANILLA_P1);
		assertThat(TBC.getEarliestNonPrepatchPhase()).isEqualTo(TBC_P1);
		assertThat(WOTLK.getEarliestNonPrepatchPhase()).isEqualTo(WOTLK_P1);
	}

	@Test
	void getLastPhase() {
		assertThat(VANILLA.getLastPhase()).isEqualTo(VANILLA_P6);
		assertThat(TBC.getLastPhase()).isEqualTo(TBC_P5);
		assertThat(WOTLK.getLastPhase()).isEqualTo(WOTLK_P1);
	}

	@Test
	void getPrepatchPhase() {
		assertThat(VANILLA.getPrepatchPhase()).isEmpty();
		assertThat(TBC.getPrepatchPhase()).hasValue(TBC_P0);
		assertThat(WOTLK.getPrepatchPhase()).hasValue(WOTLK_P0);
	}

	@Test
	void getPreviousVersion() {
		assertThat(VANILLA.getPreviousVersion()).isEmpty();
		assertThat(TBC.getPreviousVersion()).hasValue(VANILLA);
		assertThat(WOTLK.getPreviousVersion()).hasValue(TBC);
	}

	@Test
	void getPhasesStartingFrom() {
		assertThat(TBC.getPhasesStartingFrom(TBC_P3)).isEqualTo(List.of(TBC_P3, TBC_P4, TBC_P5));
	}
}
