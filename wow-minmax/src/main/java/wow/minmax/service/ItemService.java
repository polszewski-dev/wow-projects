package wow.minmax.service;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	Item getItem(int itemId);

	List<Item> getItems();

	List<Item> getItems(int phase);

	List<Item> getItems(int phase, ItemSlot slot);

	Map<ItemSlot, List<Item>> getItemsBySlot(int phase, CharacterClass characterClass, SpellSchool spellSchool);

	List<Enchant> getAvailableEnchants(Item item, int phase);

	List<Enchant> getCasterEnchants(ItemType itemType, int phase, SpellSchool spellSchool);

	List<Gem> getAvailableGems(Item item, int socketNo, int phase, boolean onlyCrafted);

	List<Gem[]> getCasterGemCombos(Item item, int phase);
}