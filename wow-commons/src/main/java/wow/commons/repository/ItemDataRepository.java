package wow.commons.repository;

import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Raid;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ItemDataRepository {
	Item getItem(int itemId);
	Item getItem(String itemName);
	Item getItem(ItemLink itemLink);

	Collection<Item> getAllItems();

	List<Item> getCasterItems(int phase, CharacterClass characterClass, SpellSchool spellSchool);

	Map<ItemType, List<Item>> getCasterItemsByType(int phase, CharacterClass characterClass, SpellSchool spellSchool);

	int getPhase(Item item);

	Set<Raid> getRaidSources(Item item);

	List<Item> getTierItems(ItemSet tierSet);
	List<Item> getItemsTradedFor(ItemLink itemLink);
	List<Item> getSourceItemsFor(ItemLink itemLink);
	List<Item> getEquippableItemsFromRaidDrop(Item item, CharacterClass clazz);

	Collection<ItemSet> getAllItemSets();
	ItemSet getItemSet(String name);
	Enchant getEnchant(int enchantId);
	Enchant getEnchant(String name);
	Gem getGem(int gemId);
	Gem getGem(String name);
	List<Gem> getAllGems();

	List<Enchant> getEnchants(ItemType itemType);
}
