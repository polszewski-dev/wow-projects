package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuildId;
import wow.character.model.character.Character;
import wow.character.model.character.CharacterProfessions;
import wow.character.model.character.Enemy;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.character.service.ItemService;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

	private final CharacterService characterService;
	private final ItemService itemService;
	private final SpellService spellService;
	private final UpgradeService upgradeService;

	@Override
	public List<PlayerProfileInfo> getPlayerProfileInfos() {
		return playerProfileRepository.getPlayerProfileList().stream()
				.map(this::getPlayerProfile)
				.map(PlayerProfile::getProfileInfo)
				.toList();
	}

	@Override
	public PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo) {
		PlayerProfile playerProfile = createTemporaryPlayerProfile(
				UUID.randomUUID(),
				playerProfileInfo.getProfileName(),
				playerProfileInfo.getCharacterClassId(),
				playerProfileInfo.getRaceId(),
				playerProfileInfo.getLevel(),
				playerProfileInfo.getBuildId(),
				playerProfileInfo.getProfessions(),
				playerProfileInfo.getEnemyType(),
				playerProfileInfo.getPhaseId()
		);

		saveProfile(playerProfile);
		return playerProfile;
	}

	@Override
	public PlayerProfile createTemporaryPlayerProfile(
			UUID profileId, String profileName, CharacterClassId characterClassId, RaceId raceId, int level, BuildId buildId, CharacterProfessions professions, CreatureType enemyType, PhaseId phaseId
	) {
		Character character = characterService.createCharacter(characterClassId, raceId, level, buildId, professions, phaseId);
		Enemy enemy = characterService.createEnemy(enemyType);

		character.setTargetEnemy(enemy);

		return new PlayerProfile(
				profileId,
				profileName,
				character
		);
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return getPlayerProfile(playerProfileRepository.getPlayerProfile(profileId).orElseThrow());
	}

	@Override
	public PlayerProfile changeItemBestVariant(UUID profileId, ItemSlot slot, int itemId) {
		EquippableItem bestItemVariant = getBestItemVariant(profileId, slot, itemId);

		return changeItem(profileId, slot, bestItemVariant);
	}

	private EquippableItem getBestItemVariant(UUID profileId, ItemSlot slot, int itemId) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);
		Item item = itemService.getItem(itemId, playerProfile.getPhase());
		return upgradeService.getBestItemVariant(playerProfile.getCharacter(), item, slot, playerProfile.getDamagingSpell());
	}

	@Override
	public PlayerProfile changeItem(UUID profileId, ItemSlot slot, EquippableItem item) {
		PlayerProfile playerProfile = getPlayerProfile(profileId);

		playerProfile.equip(item, slot);
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

		if (slotGroup == ItemSlotGroup.WEAPONS && items.size() == 1) {
			items = Arrays.asList(items.get(0), null);
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
		var converterParams = createParams(profile.getPhaseId(), this);
		return playerProfilePOConverter.convertBack(profile, converterParams);
	}
}
