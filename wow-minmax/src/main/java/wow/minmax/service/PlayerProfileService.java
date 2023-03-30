package wow.minmax.service;

import wow.character.model.build.BuildId;
import wow.character.model.character.CharacterProfessions;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
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

	PlayerProfile createTemporaryPlayerProfile(
			UUID profileId, String profileName, CharacterClassId characterClassId, RaceId raceId, int level, BuildId buildId, CharacterProfessions professions, CreatureType enemyType, PhaseId phaseId);

	PlayerProfile getPlayerProfile(UUID profileId);

	PlayerProfile changeItemBestVariant(UUID profileId, ItemSlot slot, int itemId);

	PlayerProfile changeItem(UUID profileId, ItemSlot slot, EquippableItem item);

	PlayerProfile changeItemGroup(UUID profileId, ItemSlotGroup slotGroup, List<EquippableItem> items);

	PlayerProfile resetEquipment(UUID profileId);

	PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled);
}
