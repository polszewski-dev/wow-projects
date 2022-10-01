package wow.minmax.service;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Item;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<Comparison> findUpgrades(PlayerProfile playerProfile, ItemSlotGroup slotGroup, SpellId spellId);

	EquippableItem getBestItemVariant(PlayerProfile playerProfile, Item item, ItemSlot itemSlot, SpellId spellId);

	// static score based on item stats

	double getItemScore(Item item, SpellSchool shadow);
}
