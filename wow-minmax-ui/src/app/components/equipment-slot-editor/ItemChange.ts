import { EquippableItem } from "src/app/model/equipment/EquippableItem";
import { ItemSlot } from "src/app/model/equipment/ItemSlot";

export interface ItemChange {
	itemSlot: ItemSlot;
	item?: EquippableItem;
}
