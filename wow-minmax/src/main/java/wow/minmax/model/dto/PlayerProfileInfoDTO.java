package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.character.model.build.BuildId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerProfileInfoDTO {
	private UUID profileId;
	private String profileName;
	private CharacterClassId characterClass;
	private RaceId race;
	private int level;
	private CreatureType enemyType;
	private BuildId buildId;
	private List<CharacterProfessionDTO> professions;
	private PhaseId phase;
	private LocalDateTime lastModified;
}
