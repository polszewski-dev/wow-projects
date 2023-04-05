package wow.minmax.service;

import wow.character.model.character.Character;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.commons.model.spells.Spell;
import wow.minmax.model.Comparison;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface UpgradeService {
	List<Comparison> findUpgrades(Character character, ItemSlotGroup slotGroup, ItemFilter itemFilter, Spell spell);

	EquippableItem getBestItemVariant(Character character, Item item, ItemSlot itemSlot, Spell spell);
}
