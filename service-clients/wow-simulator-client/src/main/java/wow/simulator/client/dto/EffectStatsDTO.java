package wow.simulator.client.dto;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
public record EffectStatsDTO(
		int effectId,
		double uptime,
		double count
) {
}
