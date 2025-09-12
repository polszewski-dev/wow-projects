package wow.estimator.client.dto.stats;

/**
 * User: POlszewski
 * Date: 2024-03-28
 */
public record TalentStatsDTO(
		int talentId,
		String statEquivalent,
		double spEquivalent
) {
}
