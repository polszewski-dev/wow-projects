package wow.minmax.client.dto.simulation;

import wow.minmax.client.dto.SpellDTO;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
public record SimulationCooldownStatsDTO(
		SpellDTO spell,
		double uptime
) {
}
