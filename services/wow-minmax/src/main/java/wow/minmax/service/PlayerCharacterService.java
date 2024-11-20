package wow.minmax.service;

import wow.character.model.character.BuffListType;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.model.config.ViewConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerCharacterService {
	PlayerCharacter getCharacter(CharacterId characterId);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant);

	PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	PlayerCharacter resetEquipment(CharacterId characterId);

	PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled);

	ViewConfig getViewConfig(PlayerCharacter character);
}
