package wow.commons.model.pve;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-02-17
 */
class PhaseIdTest {
	@ParameterizedTest
	@CsvSource({
			"VANILLA_P1, ",
			"VANILLA_P2, VANILLA_P1",
			"VANILLA_P2_5, VANILLA_P2",
			"VANILLA_P3, VANILLA_P2_5",
			"VANILLA_P4, VANILLA_P3",
			"VANILLA_P5, VANILLA_P4",
			"VANILLA_P6, VANILLA_P5",
			"TBC_P0, VANILLA_P6",
			"TBC_P1, TBC_P0",
			"TBC_P2, TBC_P1",
			"TBC_P3, TBC_P2",
			"TBC_P4, TBC_P3",
			"TBC_P5, TBC_P4",
	})
	void getPreviousPhase(PhaseId phaseId, PhaseId expected) {
		assertThat(phaseId.getPreviousPhase()).isEqualTo(Optional.ofNullable(expected));
	}
}
