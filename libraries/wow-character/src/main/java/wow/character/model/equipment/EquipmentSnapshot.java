package wow.character.model.equipment;

import wow.commons.model.categorization.ItemSlot;

import java.util.EnumMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2026-11-26
 */
public record EquipmentSnapshot(Map<ItemSlot, EquippableItem> itemsBySlot) {
	public Map<ItemSlot, EquippableItem> getChangedSlots(EquipmentSnapshot oldEquipment) {
		var result = new EnumMap<ItemSlot, EquippableItem>(ItemSlot.class);

		for (var itemSlot : ItemSlot.getDpsSlots()) {
			var oldItem = oldEquipment.get(itemSlot);
			var newItem = this.get(itemSlot);

			if (!((oldItem == newItem) || (oldItem != null && oldItem.isTheSameAs(newItem)))) {
				result.put(itemSlot, newItem);
			}
		}

		return result;
	}

	public EquippableItem get(ItemSlot slot) {
		return itemsBySlot.get(slot);
	}
}
