package wow.simulator.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
public record StatsDTO(
		List<AbilityStatsDTO> abilityStats,
		double simulationDuration,
		int totalDamage,
		int dps
) {
}
