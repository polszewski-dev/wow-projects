package wow.minmax.client.dto;

import org.junit.jupiter.api.Test;
import wow.commons.client.dto.CharacterClassDTO;
import wow.commons.client.dto.RaceDTO;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class PlayerCharacterDTOTest {
	@Test
	void withCharacterId() {
		var characterClass = new CharacterClassDTO(CharacterClassId.WARLOCK, "class", "icon", List.of(), List.of());
		var race = new RaceDTO(RaceId.ORC, "race", "icon", List.of());
		var dto = new PlayerCharacterDTO(null, characterClass, race);
		var changed = dto.withCharacterId("characterId");

		assertThat(changed).isEqualTo(new PlayerCharacterDTO(
				"characterId", characterClass, race
		));
	}
}