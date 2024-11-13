package wow.commons.client.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.client.WowCommonsClientSpringTest;
import wow.commons.client.dto.CharacterClassDTO;
import wow.commons.client.dto.RaceDTO;
import wow.commons.model.character.RaceId;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.character.RaceRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.GameVersionId.TBC;
import static wow.commons.model.spell.SpellSchool.FIRE;
import static wow.commons.model.spell.SpellSchool.SHADOW;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class CharacterClassConverterTest extends WowCommonsClientSpringTest {
	@Autowired
	CharacterClassConverter characterClassConverter;

	@Autowired
	CharacterClassRepository characterClassRepository;

	@Autowired
	RaceRepository raceRepository;

	@Autowired
	RaceConverter raceConverter;

	@Test
	void convert() {
		var characterClass = characterClassRepository.getCharacterClass(WARLOCK, TBC).orElseThrow();

		var converted = characterClassConverter.convert(characterClass);

		assertThat(converted).isEqualTo(
				new CharacterClassDTO(
						WARLOCK,
						"Warlock",
						"classicon_warlock",
						List.of(
								getRaceDTO(RaceId.UNDEAD),
								getRaceDTO(RaceId.ORC),
								getRaceDTO(RaceId.BLOOD_ELF),
								getRaceDTO(RaceId.HUMAN),
								getRaceDTO(RaceId.GNOME)
						),
						List.of(SHADOW, FIRE))
		);
	}

	RaceDTO getRaceDTO(RaceId raceId) {
		return raceConverter.convert(raceRepository.getRace(raceId, TBC).orElseThrow());
	}
}