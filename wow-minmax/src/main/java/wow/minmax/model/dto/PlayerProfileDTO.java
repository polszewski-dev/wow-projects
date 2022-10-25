package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.Race;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class PlayerProfileDTO {
	private final UUID profileId;
	private final String profileName;

	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final CreatureType enemyType;

	private final Phase phase;

	private EquipmentDTO equipment;
	private List<BuffDTO> buffs;
	private List<TalentDTO> talents;

	private LocalDateTime lastModified;
}
