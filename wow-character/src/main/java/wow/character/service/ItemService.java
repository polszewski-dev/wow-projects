package wow.character.service;

import wow.character.model.character.Character;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Phase;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public interface ItemService {
	Item getItem(int itemId, Phase phase);

	Enchant getEnchant(int enchantId, Phase phase);

	Gem getGem(int gemId, Phase phase);

	List<Item> getItemsBySlot(Character character, ItemSlot itemSlot);

	List<Enchant> getEnchants(Character character, ItemType itemType);

	List<Enchant> getBestEnchants(Character character, ItemType itemType);

	List<Gem> getGems(Character character, SocketType socketType, boolean nonUniqueOnly);

	List<Gem> getBestGems(Character character, SocketType socketType);

	List<Gem[]> getBestGemCombos(Character character, Item item);
}
