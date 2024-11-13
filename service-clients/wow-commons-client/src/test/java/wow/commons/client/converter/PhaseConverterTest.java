package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.PhaseDTO;
import wow.commons.repository.pve.PhaseRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class PhaseConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	PhaseConverter phaseConverter;

	@Autowired
	PhaseRepository phaseRepository;

	@Test
	void convert() {
		var phase = phaseRepository.getPhase(TBC_P5).orElseThrow();
		var converted = phaseConverter.convert(phase);

		assertThat(converted).isEqualTo(
				new PhaseDTO(TBC_P5, "TBC P5", 70)
		);
	}
}