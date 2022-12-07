package wow.minmax.service;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Phase;
import wow.minmax.model.PlayerProfile;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	Item getItem(int itemId, Phase phase);

	List<Item> getItemsBySlot(PlayerProfile playerProfile, ItemSlot itemSlot);

	List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType);

	List<Enchant> getBestEnchants(PlayerProfile playerProfile, ItemType itemType);

	List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean nonUniqueOnly);

	List<Gem> getBestGems(PlayerProfile playerProfile, SocketType socketType);

	List<Gem[]> getBestGemCombos(PlayerProfile playerProfile, Item item);
}
