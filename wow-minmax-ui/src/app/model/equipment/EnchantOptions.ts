import { Enchant } from "./Enchant";
import { ItemSubType } from "./ItemSubType";
import { ItemType } from "./ItemType";

export interface EnchantOptions {
	itemType: ItemType;
	itemSubType: ItemSubType;
	enchants: Enchant[];
}
