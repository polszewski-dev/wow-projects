package wow.minmax.client.dto;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record ConsumableStatusDTO(
		ConsumableDTO consumable,
		boolean enabled
) {
}
