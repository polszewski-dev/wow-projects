package wow.evaluator.service;

import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.evaluator.model.Player;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	List<Item> getItemsBySlot(Player player, ItemSlot itemSlot, ItemFilter itemFilter);

	List<Enchant> getBestEnchants(Player player, ItemType itemType, ItemSubType itemSubType);

	List<Gem> getGems(Player player, SocketType socketType, boolean uniqueness);

	List<Gem> getBestNonUniqueGems(Player player, SocketType socketType);

	List<Gem[]> getBestGemCombos(Player player, Item item, ItemSlotGroup slotGroup, GemFilter gemFilter);
}
