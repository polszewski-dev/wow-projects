import { Enchant } from './Enchant';
import { Gem } from './Gem';
import { Item } from './Item';

export interface EquippableItem {
	item: Item;
	enchant: Enchant | null;
	gems: (Gem | null)[];
}
