import { EquippableItem } from './EquippableItem';
import { ItemSlot } from './ItemSlot';

export interface Equipment {
	itemsBySlot: Record<ItemSlot, EquippableItem | undefined>;
}
