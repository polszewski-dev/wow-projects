package wow.evaluator.client.dto.upgrade;

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
