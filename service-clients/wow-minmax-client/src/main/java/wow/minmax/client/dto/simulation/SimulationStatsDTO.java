package wow.minmax.client.dto.simulation;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
public record SimulationStatsDTO(
		List<SimulationAbilityStatsDTO> abilityStats,
		double simulationDuration,
		int totalDamage,
		int dps,
		int numCasts
) {
}
