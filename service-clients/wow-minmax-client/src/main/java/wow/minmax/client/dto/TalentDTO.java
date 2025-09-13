package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record TalentDTO(
		int id,
		String name,
		int rank,
		int maxRank,
		String icon,
		String tooltip
) {
}
