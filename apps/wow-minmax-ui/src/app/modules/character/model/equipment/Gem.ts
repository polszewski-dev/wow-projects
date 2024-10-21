import { GemColor } from './GemColor';
import { ItemRarity } from './ItemRarity';

export interface Gem {
	id: number;
	name: string;
	color: GemColor;
	rarity: ItemRarity;
	source: string;
	shortName: string;
	icon: string;
	tooltip: string;
}
