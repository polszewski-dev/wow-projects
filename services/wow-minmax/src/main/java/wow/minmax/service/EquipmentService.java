package wow.minmax.service;

import wow.character.model.character.GearSet;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.CharacterId;
import wow.minmax.model.equipment.EquipmentSocketStatus;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
public interface EquipmentService {
	Equipment getEquipment(CharacterId characterId);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter);

	PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	PlayerCharacter resetEquipment(CharacterId characterId);

	EquipmentSocketStatus getEquipmentSocketStatus(CharacterId characterId);

	List<GearSet> getAvailableGearSets(CharacterId characterId);

	PlayerCharacter equipGearSet(CharacterId characterId, String gearSet);

	PlayerCharacter equipPreviousPhase(CharacterId characterId);
}
