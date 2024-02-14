import { ItemRarity } from './ItemRarity';

export interface Enchant {
	id: number;
	name: string;
	rarity: ItemRarity;
	icon: string;
	tooltip: string;
}
