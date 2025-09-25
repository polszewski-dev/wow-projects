package wow.simulator.client.dto;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
public record StatsDTO(
		List<AbilityStatsDTO> abilityStats,
		List<EffectStatsDTO> effectStats,
		List<CooldownStatsDTO> cooldownStats,
		double simulationDuration,
		int totalDamage,
		int dps,
		int numCasts
) {
}
