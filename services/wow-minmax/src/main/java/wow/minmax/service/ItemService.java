package wow.minmax.service;

import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	List<Item> getItemsBySlot(PlayerCharacter player, ItemSlot itemSlot, ItemFilter itemFilter);

	List<Enchant> getEnchants(PlayerCharacter player, ItemType itemType, ItemSubType itemSubType);

	List<Gem> getGems(PlayerCharacter player, SocketType socketType);

	List<Gem> getGems(PlayerCharacter player, SocketType socketType, boolean uniqueness);
}
