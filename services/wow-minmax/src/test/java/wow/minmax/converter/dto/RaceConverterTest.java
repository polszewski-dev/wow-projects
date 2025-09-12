package wow.minmax.converter.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.repository.character.RaceRepository;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.client.dto.RaceDTO;
import wow.minmax.client.dto.RacialDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.GameVersionId.TBC;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class RaceConverterTest extends WowMinMaxSpringTest {
	@Autowired
	RaceConverter raceConverter;

	@Autowired
	RaceRepository raceRepository;

	@Test
	void convert() {
		var race = raceRepository.getRace(ORC, TBC).orElseThrow();

		var converted = raceConverter.convert(race);

		assertThat(converted).isEqualTo(
				new RaceDTO(ORC, "Orc", "race_orc_male", List.of(
						new RacialDTO(
								"Axe Specialization",
								"Expertise with Axes and Two-Handed Axes increased by 5.",
								"inv_axe_02",
								"Expertise with Axes and Two-Handed Axes increased by 5."
						),
						new RacialDTO(
								"Command",
								"Damage dealt by Hunter and Warlock pets increased by 5%.",
								"ability_warrior_warcry",
								"Damage dealt by Hunter and Warlock pets increased by 5%."),
						new RacialDTO(
								"Hardiness",
								"Chance to resist Stun effects increased by an additional 15%.",
								"inv_helmet_23",
								"Chance to resist Stun effects increased by an additional 15%."
						)
				))
		);
	}
}