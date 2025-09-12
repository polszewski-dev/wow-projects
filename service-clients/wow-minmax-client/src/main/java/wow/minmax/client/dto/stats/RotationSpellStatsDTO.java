package wow.minmax.client.dto.stats;

import wow.commons.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
public record RotationSpellStatsDTO(
		AbilityDTO spell,
		double numCasts,
		double damage
) {
}
