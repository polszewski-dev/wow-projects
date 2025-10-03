package wow.minmax.model.equipment;

import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;

/**
 * User: POlszewski
 * Date: 2025-10-04
 */
public record ItemSlotStatus(
		ItemSlot itemSlot,
		EquippableItem item
) {
}
