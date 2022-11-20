package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
@Data
@AllArgsConstructor
public class EquipmentSlotDTO {
	private ItemSlot slot;
	private ItemSlotGroup slotGroup;
	private EquippableItemDTO equippableItem;
	private List<ItemDTO> availableItems;
}
