package wow.simulator.client.dto;

import wow.commons.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
public record AbilityStatsDTO(
		AbilityDTO ability,
		double totalCastTime,
		int numCasts,
		int numHit,
		int numCrit,
		int totalDamage,
		int dps
) {
}
