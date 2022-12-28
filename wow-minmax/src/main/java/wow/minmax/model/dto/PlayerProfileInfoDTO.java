package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.character.model.build.BuildId;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-25
 */
@Data
@AllArgsConstructor
public class PlayerProfileInfoDTO {
	private UUID profileId;
	private String profileName;
	private CharacterClass characterClass;
	private Race race;
	private int level;
	private CreatureType enemyType;
	private BuildId buildId;
	private Phase phase;
	private LocalDateTime lastModified;
}
