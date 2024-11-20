package wow.minmax.service;

import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.minmax.model.PlayerCharacter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	List<Item> getItemsBySlot(PlayerCharacter character, ItemSlot itemSlot, ItemFilter itemFilter);

	List<Enchant> getEnchants(PlayerCharacter character, ItemType itemType, ItemSubType itemSubType);

	List<Enchant> getBestEnchants(PlayerCharacter character, ItemType itemType, ItemSubType itemSubType);

	List<Gem> getGems(PlayerCharacter character, SocketType socketType, boolean nonUniqueOnly);

	List<Gem> getBestGems(PlayerCharacter character, SocketType socketType);

	List<Gem[]> getBestGemCombos(PlayerCharacter character, Item item);
}
