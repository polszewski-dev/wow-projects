package wow.minmax.service;

import wow.character.model.character.BuffListType;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.ViewConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerCharacterService {
	PlayerCharacter getPlayer(CharacterId characterId);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter);

	PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	PlayerCharacter resetEquipment(CharacterId characterId);

	PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled);

	PlayerCharacter enableConsumable(CharacterId characterId, String consumableName, boolean enabled);

	ViewConfig getViewConfig(PlayerCharacter player);
}
