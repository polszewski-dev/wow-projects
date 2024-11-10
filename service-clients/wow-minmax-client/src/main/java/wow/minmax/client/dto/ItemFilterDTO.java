package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
public record ItemFilterDTO(
		boolean heroics,
		boolean raids,
		boolean worldBosses,
		boolean pvpItems,
		boolean greens,
		boolean legendaries
) {
}
