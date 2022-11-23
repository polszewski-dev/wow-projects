package wow.commons.repository;

import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;

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
	List<Item> getItemsByType(ItemType itemType);

	Collection<ItemSet> getAllItemSets();
	Optional<ItemSet> getItemSet(String name);

	Optional<Enchant> getEnchant(int enchantId);
	Optional<Enchant> getEnchant(String name);
	List<Enchant> getEnchants(ItemType itemType);

	Optional<Gem> getGem(int gemId);
	Optional<Gem> getGem(String name);
	List<Gem> getAllGems();

	Optional<TradedItem> getTradedItem(int tradedItemId);
}
