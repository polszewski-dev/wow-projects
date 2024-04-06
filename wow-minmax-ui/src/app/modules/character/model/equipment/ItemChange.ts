import { EquippableItem } from "./EquippableItem";
import { ItemSlot } from "./ItemSlot";

export interface ItemChange {
	itemSlot: ItemSlot;
	item?: EquippableItem;
}

export function newItemChange(itemSlot: ItemSlot, item?: EquippableItem) {
	return {
		itemSlot, item
	};
}
