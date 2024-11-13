package wow.commons.client.dto;

import wow.commons.model.talent.TalentId;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record TalentDTO(
		TalentId talentId,
		String name,
		int rank,
		int maxRank,
		String icon,
		String tooltip
) {
}
