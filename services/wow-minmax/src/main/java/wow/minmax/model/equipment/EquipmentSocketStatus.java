package wow.minmax.model.equipment;

import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record EquipmentSocketStatus(
		Map<ItemSlot, ItemSocketStatus> socketStatusesByItemSlot
) {
}
