package wow.commons.client.dto;

import wow.commons.client.dto.equipment.EquipmentDTO;
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
public record PlayerDTO(
		String name,
		CharacterClassId characterClassId,
		RaceId raceId,
		int level,
		PhaseId phaseId,
		List<CharacterProfessionDTO> professions,
		List<ExclusiveFaction> exclusiveFactions,
		EquipmentDTO equipment,
		List<Integer> talentIds,
		PveRole role,
		PetType activePet,
		String rotation,
		List<BuffDTO> buffs,
		List<ActiveEffectDTO> activeEffects,
		List<ConsumableDTO> consumables,
		NonPlayerDTO target
) {
}
