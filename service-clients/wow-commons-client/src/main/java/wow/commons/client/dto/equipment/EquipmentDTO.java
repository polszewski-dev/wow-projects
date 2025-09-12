package wow.commons.client.dto.equipment;

import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public record EquipmentDTO(
		Map<ItemSlot, EquippableItemDTO> itemsBySlot
) {
}
