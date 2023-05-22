import { EnchantOptions } from './EnchantOptions';
import { GemOptions } from './GemOptions';
import { ItemOptions } from './ItemOptions';

export interface EquipmentOptions {
	itemOptions: ItemOptions[];
	enchantOptions: EnchantOptions[];
	gemOptions: GemOptions[];
	editGems: boolean;
	heroics: boolean;
}