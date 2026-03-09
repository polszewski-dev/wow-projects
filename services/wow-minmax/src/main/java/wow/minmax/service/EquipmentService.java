package wow.minmax.service;

import wow.character.model.character.GearSet;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.equipment.EquipmentSocketStatus;
import wow.minmax.model.equipment.ItemSlotStatus;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public interface EquipmentService {
	Equipment getEquipment(PlayerId playerId);

	List<ItemSlotStatus> equipItem(PlayerId playerId, ItemSlot slot, EquippableItem item);

	List<ItemSlotStatus> equipItem(PlayerId playerId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter);

	List<ItemSlotStatus> equipItemGroup(PlayerId playerId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	Player resetEquipment(PlayerId playerId);

	EquipmentSocketStatus getEquipmentSocketStatus(PlayerId playerId);

	List<GearSet> getAvailableGearSets(PlayerId playerId);

	Player equipGearSet(PlayerId playerId, String gearSet);

	Player equipPreviousPhase(PlayerId playerId);
}
