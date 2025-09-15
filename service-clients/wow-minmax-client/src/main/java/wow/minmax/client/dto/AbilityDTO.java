package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record AbilityDTO(
		int id,
		String name,
		Integer rank,
		String icon,
		String tooltip
) {
}
