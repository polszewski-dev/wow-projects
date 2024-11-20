package wow.minmax.service;

import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.model.Upgrade;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<Upgrade> findUpgrades(PlayerCharacter character, ItemSlotGroup slotGroup, ItemFilter itemFilter);

	EquippableItem getBestItemVariant(PlayerCharacter character, Item item, ItemSlot itemSlot);
}
