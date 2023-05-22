import { Item } from "./Item";
import { ItemSlot } from "./ItemSlot";

export interface ItemOptions {
	itemSlot: ItemSlot;
	items: Item[];
}
