import { EquippableItem } from "./EquippableItem";
import { ItemSlot } from "./ItemSlot";

export interface ItemSlotStatus {
	itemSlot: ItemSlot;
	item: EquippableItem | null;
}

export type EquipmentDiff = ItemSlotStatus[];
