package wow.commons.repository;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface ItemRepository {
	Optional<Item> getItem(int itemId, PhaseId phaseId);
	Optional<Item> getItem(String name, PhaseId phaseId);
	List<Item> getItemsBySlot(ItemSlot itemSlot, PhaseId phaseId);

	Optional<ItemSet> getItemSet(String name, PhaseId phaseId);

	Optional<Enchant> getEnchant(int enchantId, PhaseId phaseId);
	Optional<Enchant> getEnchant(String name, PhaseId phaseId);
	List<Enchant> getEnchants(ItemType itemType, PhaseId phaseId);

	Optional<Gem> getGem(int gemId, PhaseId phaseId);
	Optional<Gem> getGem(String name, PhaseId phaseId);
	List<Gem> getGems(SocketType socketType, PhaseId phaseId);

	Optional<TradedItem> getTradedItem(int tradedItemId, PhaseId phaseId);
}
