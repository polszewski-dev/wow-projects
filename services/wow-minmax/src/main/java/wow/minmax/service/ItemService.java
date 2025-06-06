package wow.minmax.service;

import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.minmax.model.Player;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	List<Item> getItemsBySlot(Player player, ItemSlot itemSlot, ItemFilter itemFilter);

	List<Enchant> getEnchants(Player player, ItemType itemType, ItemSubType itemSubType);

	List<Gem> getGems(Player player, SocketType socketType);

	List<Gem> getGems(Player player, SocketType socketType, boolean uniqueness);
}
