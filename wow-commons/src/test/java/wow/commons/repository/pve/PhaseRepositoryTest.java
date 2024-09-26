package wow.commons.repository.pve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.WowCommonsSpringTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.profession.ProfessionProficiencyId.ARTISAN;
import static wow.commons.model.profession.ProfessionProficiencyId.MASTER;
import static wow.commons.model.pve.PhaseId.*;

/**
 * User: POlszewski
 * Date: 28.09.2024
 */
class PhaseRepositoryTest extends WowCommonsSpringTest {
	@Autowired
	PhaseRepository underTest;

	@Test
	void vanillaPhasesAreCorrect() {
		var phase = underTest.getPhase(VANILLA_P1).orElseThrow();

		assertThat(phase.getPhaseId()).isEqualTo(VANILLA_P1);
		assertThat(phase.getName()).isEqualTo("Vanilla P1");
		assertThat(phase.getMaxLevel()).isEqualTo(60);
		assertThat(phase.getMaxProficiencyId()).isEqualTo(ARTISAN);
	}

	@Test
	void tbcPrepatchIsCorrect() {
		var phase = underTest.getPhase(TBC_P0).orElseThrow();

		assertThat(phase.getPhaseId()).isEqualTo(TBC_P0);
		assertThat(phase.getName()).isEqualTo("TBC Pre-Patch");
		assertThat(phase.getMaxLevel()).isEqualTo(60);
		assertThat(phase.getMaxProficiencyId()).isEqualTo(ARTISAN);
	}

	@Test
	void tbcPhasesAreCorrect() {
		var phase = underTest.getPhase(TBC_P1).orElseThrow();

		assertThat(phase.getPhaseId()).isEqualTo(TBC_P1);
		assertThat(phase.getName()).isEqualTo("TBC P1");
		assertThat(phase.getMaxLevel()).isEqualTo(70);
		assertThat(phase.getMaxProficiencyId()).isEqualTo(MASTER);
	}
}