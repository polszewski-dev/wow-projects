package wow.minmax.service;

import wow.character.model.character.BuffListType;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.config.ViewConfig;

import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileService {
	List<PlayerProfileInfo> getPlayerProfileInfos();

	PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo);

	PlayerProfile getPlayerProfile(UUID profileId);

	PlayerCharacter getCharacter(CharacterId characterId);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item);

	PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant);

	PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	PlayerCharacter resetEquipment(CharacterId characterId);

	PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled);

	ViewConfig getViewConfig(PlayerCharacter character);
}
