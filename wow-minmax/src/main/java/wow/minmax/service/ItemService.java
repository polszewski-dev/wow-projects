package wow.minmax.service;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.minmax.model.PlayerProfile;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	Item getItem(int itemId);

	List<Item> getItemsByType(PlayerProfile playerProfile, ItemType itemType);

	List<Item> getItemsBySlot(PlayerProfile playerProfile, ItemSlot itemSlot);

	List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType);

	List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean onlyCrafted);

	default List<Gem> getGems(PlayerProfile playerProfile, Item item, int socketNo, boolean onlyCrafted) {
		ItemSocketSpecification specification = item.getSocketSpecification();
		return getGems(playerProfile, specification.getSocketType(socketNo), onlyCrafted);
	}

	List<Gem[]> getGemCombos(PlayerProfile playerProfile, Item item);
}
