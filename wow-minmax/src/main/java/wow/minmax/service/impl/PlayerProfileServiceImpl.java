package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.Build;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.config.ProfileConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.BuildRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.UUID;

import static wow.commons.model.character.BuffSetId.*;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final UpgradeService upgradeService;
	private final PlayerProfileRepository playerProfileRepository;
	private final ItemDataRepository itemDataRepository;
	private final SpellDataRepository spellDataRepository;
	private final BuildRepository buildRepository;
	private final ProfileConfig profileConfig;

	@Override
	public List<PlayerProfile> getPlayerProfileList() {
		return playerProfileRepository.getPlayerProfileList();
	}

	@Override
	public PlayerProfile createPlayerProfile(String profileName, Phase phase) {
		int level = phase.getGameVersion().getMaxLevel();

		Build build = getBuild(profileConfig.getDefaultBuild(), level);

		CharacterInfo characterInfo = new CharacterInfo(
				profileConfig.getDefaultClass(),
				profileConfig.getDefaultRace(),
				level,
				build,
				profileConfig.getDefaultProfessions(phase)
		);

		PlayerProfile playerProfile = new PlayerProfile(
				UUID.randomUUID(),
				profileName,
				characterInfo,
				profileConfig.getDefaultEnemyType(),
				phase
		);

		playerProfile.setBuffs(SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, Phase phase) {
		PlayerProfile copiedProfile = playerProfileRepository.getPlayerProfile(copiedProfileId).orElseThrow();
		PlayerProfile copy = copiedProfile.copy(UUID.randomUUID(), profileName, phase);

		playerProfileRepository.saveProfile(copy);
		return copy;
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Item item = itemDataRepository.getItem(itemId).orElseThrow();
		EquippableItem bestItemVariant = upgradeService.getBestItemVariant(playerProfile.copy(), item, slot, playerProfile.getDamagingSpellId());

		playerProfile.getEquipment().set(bestItemVariant, slot);
		playerProfileRepository.saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Enchant enchant = itemDataRepository.getEnchant(enchantId).orElseThrow();

		playerProfile.getEquipment().get(slot).enchant(enchant);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Gem gem = itemDataRepository.getGem(gemId).orElseThrow();

		playerProfile.getEquipment().get(slot).insertGem(socketNo, gem);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile resetEquipment(UUID profileId) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();

		playerProfile.setEquipment(new Equipment());
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled) {
		PlayerProfile playerProfile = playerProfileRepository.getPlayerProfile(profileId).orElseThrow();
		Buff buff = spellDataRepository.getBuff(buffId).orElseThrow();

		playerProfile.enableBuff(buff, enabled);
		playerProfileRepository.saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public Build getBuild(String buildId, int level) {
		return buildRepository.getBuild(buildId, level).orElseThrow();
	}
}
