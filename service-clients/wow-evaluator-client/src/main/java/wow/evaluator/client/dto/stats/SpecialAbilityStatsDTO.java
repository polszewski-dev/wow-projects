package wow.evaluator.client.dto.stats;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
public record SpecialAbilityStatsDTO(
		String description,
		String attributes,
		String statEquivalent,
		double spEquivalent,
		String sourceName,
		String sourceIcon
) {
}
