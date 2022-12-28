import { ItemSlot } from "./ItemSlot";
import { ItemSocketStatus } from "./ItemSocketStatus";

export interface EquipmentSocketStatus {
	socketStatusesByItemSlot: Record<ItemSlot, ItemSocketStatus | undefined>;
}
