package wow.minmax.service;

import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.estimator.client.dto.upgrade.UpgradeDTO;
import wow.minmax.model.Player;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<UpgradeDTO> findUpgrades(Player player, ItemSlotGroup slotGroup, ItemFilter itemFilter, GemFilter gemFilter);

	EquippableItem getBestItemVariant(Player player, Item item, ItemSlot itemSlot, GemFilter gemFilter);
}
