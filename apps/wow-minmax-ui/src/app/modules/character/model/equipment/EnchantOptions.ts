import { Enchant } from "./Enchant";
import { EquippableItem } from "./EquippableItem";
import { Item } from "./Item";
import { ItemSubType } from "./ItemSubType";
import { ItemType } from "./ItemType";

export interface EnchantOptions {
	itemType: ItemType;
	itemSubType: ItemSubType;
	enchants: Enchant[];
}

export function getMatchingEnchantOptions(enchantOptions: EnchantOptions[], equippedItem: EquippableItem) {
	return enchantOptions.find(x => isMatchingEnchant(x, equippedItem.item))?.enchants || []
}

function isMatchingEnchant(x: EnchantOptions, item: Item) {
	return x.itemType === item.itemType && x.itemSubType === item.itemSubType
}
