package wow.minmax.client.dto.stats;

import wow.minmax.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
public record SpecialAbilityStatsDTO(
		AbilityDTO ability,
		String attributes,
		String statEquivalent,
		double spEquivalent
) {
}
