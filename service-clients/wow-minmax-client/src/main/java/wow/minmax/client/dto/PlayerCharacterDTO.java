package wow.minmax.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
public record PlayerCharacterDTO(
		String characterId,
		CharacterClassDTO characterClass,
		RaceDTO race,
		List<ProfessionDTO> professions,
		ScriptInfoDTO script
) {
}
