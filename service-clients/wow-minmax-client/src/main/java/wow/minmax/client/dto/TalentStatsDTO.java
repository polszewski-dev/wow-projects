package wow.minmax.client.dto;

import wow.commons.client.dto.TalentDTO;

/**
 * User: POlszewski
 * Date: 2024-03-28
 */
public record TalentStatsDTO(
		TalentDTO talent,
		String statEquivalent,
		double spEquivalent
) {
}