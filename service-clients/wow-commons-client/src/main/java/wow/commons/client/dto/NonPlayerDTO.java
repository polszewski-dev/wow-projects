package wow.commons.client.dto;

import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record NonPlayerDTO(
		String name,
		CreatureType enemyType,
		int enemyLevel,
		PhaseId phaseId
) {
}
