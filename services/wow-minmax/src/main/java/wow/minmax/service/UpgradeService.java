package wow.minmax.service;

import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.estimator.client.dto.upgrade.UpgradeDTO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<UpgradeDTO> findUpgrades(PlayerCharacter player, ItemSlotGroup slotGroup, ItemFilter itemFilter, GemFilter gemFilter);

	EquippableItem getBestItemVariant(PlayerCharacter player, Item item, ItemSlot itemSlot, GemFilter gemFilter);
}
