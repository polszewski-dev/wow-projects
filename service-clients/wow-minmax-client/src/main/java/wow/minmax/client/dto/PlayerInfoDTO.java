package wow.minmax.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
public record PlayerInfoDTO(
		String characterId,
		CharacterClassDTO characterClass,
		RaceDTO race,
		List<ProfessionDTO> professions,
		String talentLink,
		ScriptInfoDTO script
) {
}
