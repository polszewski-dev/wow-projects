package wow.minmax.model;

import wow.commons.model.item.Consumable;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record ConsumableStatus(
		Consumable consumable,
		boolean enabled
) {
}
