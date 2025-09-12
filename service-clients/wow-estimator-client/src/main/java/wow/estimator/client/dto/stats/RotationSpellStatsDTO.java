package wow.estimator.client.dto.stats;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
public record RotationSpellStatsDTO(
		int spellId,
		double numCasts,
		double damage
) {
}
