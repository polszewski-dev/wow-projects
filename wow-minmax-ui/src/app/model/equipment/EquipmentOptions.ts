import { Enchant } from './Enchant';
import { Gem } from './Gem';
import { Item } from './Item';
import { ItemSlot } from './ItemSlot';
import { ItemType } from './ItemType';
import { SocketType } from './SocketType';

export interface EquipmentOptions {
	itemsByItemSlot: Record<ItemSlot, Item[]>;
	enchantsByItemType: Record<ItemType, Enchant[]>;
	gemsBySocketType: Record<SocketType, Gem[]>;
	editGems: boolean;
	heroics: boolean;
}