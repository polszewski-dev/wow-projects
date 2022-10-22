package wow.commons.repository;

import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Raid;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ItemDataRepository {
	Optional<Item> getItem(int itemId);
	Optional<Item> getItem(String itemName);
	Optional<Item> getItem(ItemLink itemLink);

	Collection<Item> getAllItems();

	List<Item> getCasterItems(int phase, CharacterClass characterClass, SpellSchool spellSchool);

	Map<ItemType, List<Item>> getCasterItemsByType(int phase, CharacterClass characterClass, SpellSchool spellSchool);

	int getPhase(Item item);

	Set<Raid> getRaidSources(Item item);

	List<Item> getSetItems(ItemSet itemSet);
	List<Item> getItemsTradedFor(ItemLink itemLink);
	List<Item> getSourceItemsFor(ItemLink itemLink);
	List<Item> getEquippableItemsFromRaidDrop(Item item, CharacterClass clazz);

	Collection<ItemSet> getAllItemSets();
	Optional<ItemSet> getItemSet(String name);
	Optional<Enchant> getEnchant(int enchantId);
	Optional<Enchant> getEnchant(String name);
	Optional<Gem> getGem(int gemId);
	Optional<Gem> getGem(String name);
	List<Gem> getAllGems();

	List<Enchant> getEnchants(ItemType itemType);
}
