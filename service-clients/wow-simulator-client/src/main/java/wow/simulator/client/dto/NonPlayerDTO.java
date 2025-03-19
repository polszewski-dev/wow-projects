package wow.simulator.client.dto;

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
		List<ActiveEffectDTO> activeEffects
) {
}
