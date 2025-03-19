package wow.commons.client.dto;

import wow.commons.model.character.CreatureType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record NonPlayerDTO(
		String name,
		CreatureType enemyType,
		int enemyLevel,
		List<BuffDTO> buffs,
		List<ActiveEffectDTO> activeEffects
) {
}
