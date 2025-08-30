package wow.minmax.client.dto;

import wow.commons.client.dto.CharacterClassDTO;
import wow.commons.client.dto.RaceDTO;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
public record PlayerCharacterDTO(
		String characterId,
		CharacterClassDTO characterClass,
		RaceDTO race
) {
}
