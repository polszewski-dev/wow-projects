package wow.commons.repository;

import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.unit.CharacterInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ItemDataRepository {
	Optional<Item> getItem(int itemId);
	Optional<Item> getItem(String name);
	Optional<Item> getItem(ItemLink itemLink);

	Collection<Item> getAllItems();

	List<Item> getItemsTradedFor(ItemLink itemLink);
	List<Item> getSourceItemsFor(ItemLink itemLink);
	List<Item> getEquippableItemsFromRaidDrop(Item item, CharacterInfo characterInfo, Phase phase);

	Collection<ItemSet> getAllItemSets();
	Optional<ItemSet> getItemSet(String name);
	Optional<Enchant> getEnchant(int enchantId);
	Optional<Enchant> getEnchant(String name);
	Optional<Gem> getGem(int gemId);
	Optional<Gem> getGem(String name);
	List<Gem> getAllGems();

	List<Enchant> getEnchants(ItemType itemType);
}
