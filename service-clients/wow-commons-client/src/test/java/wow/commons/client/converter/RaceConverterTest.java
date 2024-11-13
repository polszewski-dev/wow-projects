package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.RaceDTO;
import wow.commons.repository.character.RaceRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class RaceConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	RaceConverter raceConverter;

	@Autowired
	RaceRepository raceRepository;

	@Test
	void convert() {
		var race = raceRepository.getRace(ORC, TBC).orElseThrow();

		var converted = raceConverter.convert(race);

		assertThat(converted).isEqualTo(
				new RaceDTO(ORC, "Orc", "race_orc_male", List.of())
		);
	}
}