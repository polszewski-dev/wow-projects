package wow.minmax.client.dto.equipment;

import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
public record EquipmentSocketStatusDTO(
		Map<ItemSlot, ItemSocketStatusDTO> socketStatusesByItemSlot
) {
}
