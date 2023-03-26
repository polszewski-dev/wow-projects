package wow.commons.repository;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ItemRepository {
	Optional<Item> getItem(int itemId, Phase phase);
	Optional<Item> getItem(String name, Phase phase);
	List<Item> getItemsBySlot(ItemSlot itemSlot, Phase phase);

	Optional<ItemSet> getItemSet(String name, Phase phase);

	Optional<Enchant> getEnchant(int enchantId, Phase phase);
	Optional<Enchant> getEnchant(String name, Phase phase);
	List<Enchant> getEnchants(ItemType itemType, Phase phase);

	Optional<Gem> getGem(int gemId, Phase phase);
	Optional<Gem> getGem(String name, Phase phase);
	List<Gem> getGems(SocketType socketType, Phase phase);

	Optional<TradedItem> getTradedItem(int tradedItemId, Phase phase);
}
