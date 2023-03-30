package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.character.model.build.BuildId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-17
 */
@Data
@AllArgsConstructor
public class PlayerProfilePO implements Serializable {
	private final UUID profileId;
	private final String profileName;
	private final CharacterClassId characterClassId;
	private final RaceId race;
	private final int level;
	private final BuildId buildId;
	private final List<CharacterProfessionPO> professions;
	private final CreatureType enemyType;
	private final PhaseId phaseId;
	private EquipmentPO equipment;
	private List<BuffPO> buffs;
	private LocalDateTime lastModified;
}
