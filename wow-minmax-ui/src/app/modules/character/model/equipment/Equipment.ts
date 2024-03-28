import { EquippableItem } from './EquippableItem';
import { ItemSlot } from './ItemSlot';

export interface Equipment {
	itemsBySlot: Partial<Record<ItemSlot, EquippableItem>>;
}
