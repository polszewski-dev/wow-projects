package wow.minmax.client.dto.stats;

import wow.minmax.client.dto.TalentDTO;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
public record TalentStatsDTO(
		TalentDTO talent,
		String statEquivalent,
		double spEquivalent
) {
}
