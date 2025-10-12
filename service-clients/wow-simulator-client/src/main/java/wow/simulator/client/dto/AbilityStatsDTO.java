package wow.simulator.client.dto;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
public record AbilityStatsDTO(
		int abilityId,
		double totalCastTime,
		int numCasts,
		int numHit,
		int numResisted,
		int numCrit,
		int totalDamage,
		int dps
) {
}
