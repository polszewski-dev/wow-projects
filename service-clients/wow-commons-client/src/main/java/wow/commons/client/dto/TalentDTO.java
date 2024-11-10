package wow.commons.client.dto;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record TalentDTO(
		String name,
		int rank,
		int maxRank,
		String icon,
		String tooltip
) {
}
