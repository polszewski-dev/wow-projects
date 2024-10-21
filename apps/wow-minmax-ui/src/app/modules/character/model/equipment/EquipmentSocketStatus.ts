import { ItemSlot } from "./ItemSlot";
import { ItemSocketStatus } from "./ItemSocketStatus";

export interface EquipmentSocketStatus {
	socketStatusesByItemSlot: Partial<Record<ItemSlot, ItemSocketStatus>>;
}
