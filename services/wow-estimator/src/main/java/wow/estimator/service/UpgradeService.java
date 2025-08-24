package wow.estimator.service;

import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.estimator.model.Player;
import wow.estimator.model.Upgrade;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<Upgrade> findUpgrades(Player player, ItemSlotGroup slotGroup, ItemFilter itemFilter, ItemLevelFilter itemLevelFilter, GemFilter gemFilter, Set<String> enchantNames, int maxUpgrades);

	EquippableItem getBestItemVariant(Player player, Item item, ItemSlot slot, GemFilter gemFilter, Set<String> enchantNames);
}
