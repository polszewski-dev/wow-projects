package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.build.BuildId;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;

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
	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final CreatureType enemyType;
	private final BuildId buildId;
	private final Phase phase;
	private final LocalDateTime lastModified;
}
