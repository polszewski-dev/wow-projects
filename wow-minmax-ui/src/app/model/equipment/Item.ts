import { ItemRarity } from './ItemRarity';
import { ItemType } from './ItemType';
import { SocketType } from './SocketType';

export interface Item {
	id: number;
	name: string;
	rarity: ItemRarity;
	itemType: ItemType;
	score: number;
	source: string;
	attributes: string;
	socketTypes: SocketType[];
	socketBonus: string;
	icon: string;
	tooltip: string;
}
