package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-27
 */
@AllArgsConstructor
@Getter
public class PlayerProfileInfo {
	private final UUID profileId;
	private final String profileName;
	private final CharacterClassId characterClassId;
	private final RaceId raceId;
	private final LocalDateTime lastModified;
	private final CharacterId lastUsedCharacterId;
}
