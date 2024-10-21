package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.repository.pve.GameVersionRepository;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final GameVersionRepository gameVersionRepository;
	private final PlayerProfileRepository playerProfileRepository;
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final PlayerProfilePOConverter playerProfilePOConverter;

	private final UpgradeService upgradeService;
	private final CharacterService characterService;

	private final ProfileConfig profileConfig;

	@Override
	public List<PlayerProfileInfo> getPlayerProfileInfos() {
		return playerProfileRepository.getPlayerProfileList().stream()
				.map(this::getPlayerProfile)
				.map(PlayerProfile::getProfileInfo)
				.toList();
	}

	@Override
	public PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo) {
		var profileId = UUID.randomUUID();

		var playerProfile = new PlayerProfile(
				profileId,
				playerProfileInfo.getProfileName(),
				playerProfileInfo.getCharacterClassId(),
				playerProfileInfo.getRaceId(),
				new HashMap<>(),
				LocalDateTime.now(),
				getDefaultCharacterId(profileId)
		);

		saveProfile(playerProfile);
		return playerProfile;
	}

	private CharacterId getDefaultCharacterId(UUID profileId) {
		var latestSupportedVersionId = profileConfig.getLatestSupportedVersionId();
		var latestSupportedVersion = gameVersionRepository.getGameVersion(latestSupportedVersionId).orElseThrow();

		var defaultPhase = latestSupportedVersion.getLastPhase();

		return new CharacterId(
				profileId,
				defaultPhase.getPhaseId(),
				defaultPhase.getMaxLevel(),
				profileConfig.getDefaultEnemyType(),
				profileConfig.getDefaultLevelDiff()
		);
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return getPlayerProfile(playerProfileRepository.getPlayerProfile(profileId).orElseThrow());
	}

	@Override
	public PlayerCharacter getCharacter(CharacterId characterId) {
		var playerProfile = getPlayerProfile(characterId.getProfileId());
		return getCharacter(playerProfile, characterId);
	}

	private PlayerCharacter getCharacter(PlayerProfile playerProfile, CharacterId characterId) {
		var character = playerProfile.getCharacter(characterId);

		if (character.isPresent()) {
			return character.orElseThrow();
		}

		var newCharacter = createCharacter(playerProfile, characterId);

		playerProfile.addCharacter(newCharacter);
		return newCharacter;
	}

	private PlayerCharacter createCharacter(PlayerProfile playerProfile, CharacterId characterId) {
		var newCharacter = characterService.createPlayerCharacter(
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.getLevel(),
				characterId.getPhaseId()
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				characterId.getEnemyType(),
				newCharacter.getLevel() + characterId.getEnemyLevelDiff(),
				characterId.getPhaseId()
		);

		newCharacter.setTarget(targetEnemy);

		characterService.applyDefaultCharacterTemplate(newCharacter);

		return newCharacter;
	}

	@Override
	public PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item) {
		return equipItem(characterId, slot, item, false);
	}

	@Override
	public PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant) {
		var playerProfile = getPlayerProfile(characterId.getProfileId());
		var character = getCharacter(playerProfile, characterId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, character);

		character.equip(itemToEquip, slot);
		saveProfile(playerProfile, character);
		return character;
	}

	private EquippableItem getItemToEquip(ItemSlot slot, EquippableItem item, boolean bestVariant, PlayerCharacter character) {
		if (bestVariant) {
			return upgradeService.getBestItemVariant(character, item.getItem(), slot);
		} else {
			return item;
		}
	}

	@Override
	public PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		var playerProfile = getPlayerProfile(characterId.getProfileId());
		var character = getCharacter(playerProfile, characterId);
		var slots = slotGroup.getSlots();

		for (var slot : slots) {
			character.equip(null, slot);
		}

		if (slotGroup == ItemSlotGroup.WEAPONS && items.size() == 1) {
			items = Arrays.asList(items.get(0), null);
		}

		for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
			var slot = slots.get(slotIdx);
			var item = items.get(slotIdx);
			character.equip(item, slot);
		}

		saveProfile(playerProfile, character);

		return character;
	}

	@Override
	public PlayerCharacter resetEquipment(CharacterId characterId) {
		var playerProfile = getPlayerProfile(characterId.getProfileId());
		var character = getCharacter(playerProfile, characterId);

		character.resetEquipment();
		saveProfile(playerProfile, character);
		return character;
	}

	@Override
	public PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled) {
		var playerProfile = getPlayerProfile(characterId.getProfileId());
		var character = getCharacter(playerProfile, characterId);

		character.getBuffList(buffListType).enable(buffId, rank, enabled);
		saveProfile(playerProfile, character);
		return character;
	}

	@Override
	public ViewConfig getViewConfig(PlayerCharacter character) {
		return minmaxConfigRepository.getViewConfig(character).orElseThrow();
	}

	private void saveProfile(PlayerProfile playerProfile, PlayerCharacter changedCharacter) {
		var characterId = playerProfile.getCharacterId(changedCharacter);
		playerProfile.setLastModifiedCharacterId(characterId);
		saveProfile(playerProfile);
	}

	private void saveProfile(PlayerProfile playerProfile) {
		var playerProfilePO = playerProfilePOConverter.convert(playerProfile);

		playerProfileRepository.saveProfile(playerProfilePO);
	}

	private PlayerProfile getPlayerProfile(PlayerProfilePO profile) {
		return playerProfilePOConverter.convertBack(profile);
	}
}
