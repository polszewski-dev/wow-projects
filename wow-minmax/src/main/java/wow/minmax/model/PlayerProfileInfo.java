package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.build.BuildId;
import wow.character.model.character.CharacterProfessions;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

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
	private final int level;
	private final CreatureType enemyType;
	private final BuildId buildId;
	private final CharacterProfessions professions;
	private final PhaseId phaseId;
	private final LocalDateTime lastModified;
}
