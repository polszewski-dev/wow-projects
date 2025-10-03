package wow.minmax.client.dto.equipment;

import wow.commons.model.categorization.ItemSlot;

/**
 * User: POlszewski
 * Date: 2025-10-04
 */
public record ItemSlotStatusDTO(
		ItemSlot itemSlot,
		EquippableItemDTO item
) {
}
