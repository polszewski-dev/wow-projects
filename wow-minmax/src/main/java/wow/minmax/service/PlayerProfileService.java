package wow.minmax.service;

import wow.character.model.character.Character;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;

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

	Character getCharacter(CharacterId characterId);

	Character changeItemBestVariant(CharacterId characterId, ItemSlot slot, int itemId);

	Character changeItem(CharacterId characterId, ItemSlot slot, EquippableItem item);

	Character changeItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	Character resetEquipment(CharacterId characterId);

	Character enableBuff(CharacterId characterId, int buffId, boolean enabled);
}
