package wow.minmax.client.dto;

import wow.commons.client.dto.CharacterClassDTO;
import wow.commons.client.dto.RaceDTO;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-25
 */
public record PlayerProfileInfoDTO(
		UUID profileId,
		String profileName,
		CharacterClassDTO characterClass,
		RaceDTO race,
		LocalDateTime lastModified,
		String lastUsedCharacterId
) {
}
