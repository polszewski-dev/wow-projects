package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuildId;
import wow.character.model.character.Character;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.Enemy;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.character.service.ItemService;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static wow.minmax.converter.persistent.PoConverterParams.createParams;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final PlayerProfileRepository playerProfileRepository;
	private final PlayerProfilePOConverter playerProfilePOConverter;
	private final ProfileConfig profileConfig;

	private final CharacterService characterService;
	private final ItemService itemService;
	private final SpellService spellService;
	private final UpgradeService upgradeService;

	@Override
	public List<PlayerProfileInfo> getPlayerProfileInfos() {
		return playerProfileRepository.getPlayerProfileList().stream()
				.map(this::getPlayerProfile)
				.map(this::getPlayerProfileInfo)
				.collect(Collectors.toList());
	}

	@Override
	public PlayerProfile createPlayerProfile(String profileName, Phase phase) {
		PlayerProfile playerProfile = createTemporaryPlayerProfile(
				UUID.randomUUID(),
				profileName,
				profileConfig.getDefaultClass(),
				profileConfig.getDefaultRace(),
				phase.getGameVersion().getMaxLevel(),
				profileConfig.getDefaultBuild(),
				profileConfig.getDefaultProfessions(phase), profileConfig.getDefaultEnemyType(),
				phase
		);
		
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile createTemporaryPlayerProfile(
			UUID profileId, String profileName, CharacterClass characterClass, Race race, int level, BuildId buildId, List<CharacterProfession> professions, CreatureType enemyType, Phase phase
	) {
		Character character = characterService.createCharacter(characterClass, race, level, buildId, professions, phase);
		Enemy enemy = characterService.createEnemy(enemyType);

		character.setTargetEnemy(enemy);

		return new PlayerProfile(
				profileId,
				profileName,
				character
		);
	}

	@Override
	public PlayerProfile copyPlayerProfile(UUID copiedProfileId, String profileName, Phase phase) {
		PlayerProfile copiedProfile = getPlayerProfile(copiedProfileId);
		PlayerProfile copy = copiedProfile.copy(UUID.randomUUID(), profileName, phase);

		saveProfile(copy);
		return copy;
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return getPlayerProfile(playerProfileRepository.getPlayerProfile(profileId).orElseThrow());
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Item item = itemService.getItem(itemId, playerProfile.getPhase());
		EquippableItem bestItemVariant = upgradeService.getBestItemVariant(playerProfile.getCharacter(), item, slot, playerProfile.getDamagingSpell());

		playerProfile.equip(bestItemVariant, slot);
		saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeItemGroup(UUID profileId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		List<ItemSlot> slots = slotGroup.getSlots();

		for (ItemSlot slot : slots) {
			playerProfile.equip(null, slot);
		}

		for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
			ItemSlot slot = slots.get(slotIdx);
			EquippableItem item = items.get(slotIdx);
			playerProfile.equip(item, slot);
		}

		saveProfile(playerProfile);

		return playerProfile;
	}

	@Override
	public PlayerProfile changeEnchant(UUID profileId, ItemSlot slot, int enchantId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Enchant enchant = itemService.getEnchant(enchantId, playerProfile.getPhase());

		playerProfile.getEquippedItem(slot).enchant(enchant);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile changeGem(UUID profileId, ItemSlot slot, int socketNo, int gemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Gem gem = itemService.getGem(gemId, playerProfile.getPhase());

		playerProfile.getEquippedItem(slot).insertGem(socketNo, gem);
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile resetEquipment(UUID profileId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);

		playerProfile.resetEquipment();
		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile enableBuff(UUID profileId, int buffId, boolean enabled) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Buff buff = spellService.getBuff(buffId, playerProfile.getPhase());

		playerProfile.enableBuff(buff, enabled);
		saveProfile(playerProfile);
		return playerProfile;
	}

	private void saveProfile(PlayerProfile playerProfile) {
		PlayerProfilePO playerProfilePO = playerProfilePOConverter.convert(playerProfile);

		playerProfileRepository.saveProfile(playerProfilePO);
	}

	private PlayerProfile getPlayerProfile(PlayerProfilePO profile) {
		var converterParams = createParams(profile.getPhase(), this);
		return playerProfilePOConverter.convertBack(profile, converterParams);
	}

	private PlayerProfileInfo getPlayerProfileInfo(PlayerProfile playerProfile) {
		return new PlayerProfileInfo(
				playerProfile.getProfileId(),
				playerProfile.getProfileName(),
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getEnemyType(),
				playerProfile.getBuildId(),
				playerProfile.getPhase(),
				playerProfile.getLastModified()
		);
	}
}
