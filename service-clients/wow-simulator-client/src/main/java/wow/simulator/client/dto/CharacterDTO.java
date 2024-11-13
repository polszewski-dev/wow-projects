package wow.simulator.client.dto;

import wow.commons.client.dto.BuffDTO;
import wow.commons.client.dto.CharacterProfessionDTO;
import wow.commons.client.dto.EquipmentDTO;
import wow.commons.client.dto.TalentDTO;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record CharacterDTO(
		String name,
		CharacterClassId characterClassId,
		RaceId raceId,
		int level,
		PhaseId phaseId,
		List<CharacterProfessionDTO> professions,
		List<ExclusiveFaction> exclusiveFactions,
		EquipmentDTO equipment,
		List<TalentDTO> talents,
		List<BuffDTO> buffs,
		PveRole role,
		PetType activePet,
		String rotation
) {
}
