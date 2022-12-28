import { ItemRarity } from './ItemRarity';

export interface Enchant {
	id: number;
	name: string;
	rarity: ItemRarity;
	attributes: string;
	icon: string;
	tooltip: string;
}
