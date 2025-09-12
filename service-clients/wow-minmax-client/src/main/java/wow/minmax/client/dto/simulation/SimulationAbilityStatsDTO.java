package wow.minmax.client.dto.simulation;

import wow.minmax.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
public record SimulationAbilityStatsDTO(
		AbilityDTO ability,
		double totalCastTime,
		int numCasts,
		int numHit,
		int numCrit,
		int totalDamage,
		int dps
) {
}
