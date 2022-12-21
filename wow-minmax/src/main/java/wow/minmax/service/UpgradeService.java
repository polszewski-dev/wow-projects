package wow.minmax.service;

import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<Comparison> findUpgrades(PlayerProfile playerProfile, ItemSlotGroup slotGroup, Spell spell);

	EquippableItem getBestItemVariant(PlayerProfile playerProfile, Item item, ItemSlot itemSlot, Spell spell);
}
