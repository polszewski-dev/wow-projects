package wow.minmax.client.dto.simulation;

import wow.minmax.client.dto.EffectDTO;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
public record SimulationEffectStatsDTO(
		EffectDTO effect,
		double uptime
) {
}
