package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.minmax.model.CharacterId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-17
 */
@AllArgsConstructor
@Getter
@Setter
public class PlayerProfilePO implements Serializable {
	private UUID profileId;
	private String profileName;
	private CharacterClassId characterClassId;
	private RaceId raceId;
	private Map<CharacterId, PlayerCharacterPO> characterByKey;
	private LocalDateTime lastModified;
	private CharacterId lastModifiedCharacterId;
}
