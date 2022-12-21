package wow.minmax.service;

import wow.character.model.build.BuildId;
import wow.character.model.character.CharacterProfession;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.minmax.model.PlayerProfile;

import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileService {
	List<PlayerProfile> getPlayerProfileList();

	PlayerProfile createPlayerProfile(String profileName, Phase phase);

	PlayerProfile createTemporaryPlayerProfile(
			UUID profileId, String profileName, CharacterClass characterClass, Race race, int level, BuildId buildId, List<CharacterProfession> professions, CreatureType enemyType, Phase phase);

	PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, Phase phase);

	PlayerProfile getPlayerProfile(UUID profileId);

	PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId);

	PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId);

	PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId);

	PlayerProfile resetEquipment(UUID profileId);

	PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled);
}
