package wow.minmax.service;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.minmax.model.PlayerProfile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	Item getItem(int itemId);

	List<Item> getItems();

	List<Item> getItems(Phase phase);

	List<Item> getItems(Phase phase, ItemSlot slot);

	Map<ItemType, List<Item>> getItemsByType(PlayerProfile playerProfile);

	Map<ItemSlot, List<Item>> getItemsBySlot(PlayerProfile playerProfile);

	List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType);

	List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean onlyCrafted);

	default List<Gem> getGems(PlayerProfile playerProfile, Item item, int socketNo, boolean onlyCrafted) {
		ItemSocketSpecification specification = item.getSocketSpecification();

		if (socketNo > specification.getSocketCount()) {
			return Collections.emptyList();//sortable list is needed
		}

		return getGems(playerProfile, specification.getSocketType(socketNo), onlyCrafted);
	}

	List<Gem[]> getGemCombos(PlayerProfile playerProfile, Item item);
}
