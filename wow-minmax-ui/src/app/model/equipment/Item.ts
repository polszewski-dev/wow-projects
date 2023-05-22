import { ItemRarity } from './ItemRarity';
import { ItemSubType } from './ItemSubType';
import { ItemType } from './ItemType';
import { SocketType } from './SocketType';

export interface Item {
	id: number;
	name: string;
	rarity: ItemRarity;
	itemType: ItemType;
	itemSubType: ItemSubType;
	score: number;
	source: string;
	detailedSource: string;
	attributes: string;
	socketTypes: SocketType[];
	socketBonus: string;
	icon: string;
	tooltip: string;
}
