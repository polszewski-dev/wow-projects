package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.BuildId;
import wow.character.model.character.Character;
import wow.character.model.character.Enemy;
import wow.character.model.character.Phase;
import wow.character.model.equipment.EquippableItem;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;
import wow.character.service.ItemService;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.character.CreatureType;
import wow.commons.model.item.Item;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.pve.PhaseId;
import wow.minmax.converter.persistent.PlayerProfilePOConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Service
@AllArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
	private final CharacterRepository characterRepository;
	private final PlayerProfileRepository playerProfileRepository;
	private final PlayerProfilePOConverter playerProfilePOConverter;

	private final ItemService itemService;
	private final SpellService spellService;
	private final UpgradeService upgradeService;
	private final CharacterService characterService;

	@Override
	public List<PlayerProfileInfo> getPlayerProfileInfos() {
		return playerProfileRepository.getPlayerProfileList().stream()
				.map(this::getPlayerProfile)
				.map(PlayerProfile::getProfileInfo)
				.toList();
	}

	@Override
	public PlayerProfile createPlayerProfile(PlayerProfileInfo playerProfileInfo) {
		UUID profileId = UUID.randomUUID();

		PlayerProfile playerProfile = new PlayerProfile(
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
		PhaseId defaultPhaseId = PhaseId.TBC_P5;//todo getSupportedVersions().last().getPhases().last
		Phase defaultPhase = characterRepository.getPhase(defaultPhaseId).orElseThrow();

		return new CharacterId(
				profileId,
				defaultPhase.getPhaseId(),
				defaultPhase.getGameVersion().getMaxLevel(),
				CreatureType.BEAST,
				3
		);
	}

	@Override
	public PlayerProfile getPlayerProfile(UUID profileId) {
		return getPlayerProfile(playerProfileRepository.getPlayerProfile(profileId).orElseThrow());
	}

	@Override
	public Character getCharacter(CharacterId characterId) {
		PlayerProfile playerProfile = getPlayerProfile(characterId.getProfileId());
		return getCharacter(playerProfile, characterId);
	}

	private Character getCharacter(PlayerProfile playerProfile, CharacterId characterId) {
		Optional<Character> character = playerProfile.getCharacter(characterId);

		if (character.isPresent()) {
			return character.orElseThrow();
		}

		Character newCharacter = createCharacter(playerProfile, characterId);

		playerProfile.addCharacter(newCharacter);
		return newCharacter;
	}

	private Character createCharacter(PlayerProfile playerProfile, CharacterId characterId) {
		Character newCharacter = characterService.createCharacter(
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.getLevel(),
				BuildId.DESTRO_SHADOW,//todo
				characterId.getPhaseId()
		);

		Enemy targetEnemy = new Enemy(characterId.getEnemyType(), characterId.getEnemyLevelDiff());

		newCharacter.setTargetEnemy(targetEnemy);
		newCharacter.addProfession(ProfessionId.TAILORING);//todo
		newCharacter.addProfession(ProfessionId.ENCHANTING);//todo

		return newCharacter;
	}

	@Override
	public Character changeItemBestVariant(CharacterId characterId, ItemSlot slot, int itemId) {
		EquippableItem bestItemVariant = getBestItemVariant(characterId, slot, itemId);

		return changeItem(characterId, slot, bestItemVariant);
	}

	private EquippableItem getBestItemVariant(CharacterId characterId, ItemSlot slot, int itemId) {
		Character character = getCharacter(characterId);
		Item item = itemService.getItem(itemId, character.getPhaseId());
		return upgradeService.getBestItemVariant(character, item, slot, character.getDamagingSpell());
	}

	@Override
	public Character changeItem(CharacterId characterId, ItemSlot slot, EquippableItem item) {
		PlayerProfile playerProfile = getPlayerProfile(characterId.getProfileId());
		Character character = getCharacter(playerProfile, characterId);

		character.equip(item, slot);
		saveProfile(playerProfile, character);

		return character;
	}

	@Override
	public Character changeItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		PlayerProfile playerProfile = getPlayerProfile(characterId.getProfileId());
		Character character = getCharacter(playerProfile, characterId);
		List<ItemSlot> slots = slotGroup.getSlots();

		for (ItemSlot slot : slots) {
			character.equip(null, slot);
		}

		if (slotGroup == ItemSlotGroup.WEAPONS && items.size() == 1) {
			items = Arrays.asList(items.get(0), null);
		}

		for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
			ItemSlot slot = slots.get(slotIdx);
			EquippableItem item = items.get(slotIdx);
			character.equip(item, slot);
		}

		saveProfile(playerProfile, character);

		return character;
	}

	@Override
	public Character resetEquipment(CharacterId characterId) {
		PlayerProfile playerProfile = getPlayerProfile(characterId.getProfileId());
		Character character = getCharacter(playerProfile, characterId);

		character.resetEquipment();
		saveProfile(playerProfile, character);
		return character;
	}

	@Override
	public Character enableBuff(CharacterId characterId, int buffId, boolean enabled) {
		PlayerProfile playerProfile = getPlayerProfile(characterId.getProfileId());
		Character character = getCharacter(playerProfile, characterId);
		Buff buff = spellService.getBuff(buffId, character.getPhaseId());

		character.enableBuff(buff, enabled);
		saveProfile(playerProfile, character);
		return character;
	}

	private void saveProfile(PlayerProfile playerProfile, Character changedCharacter) {
		CharacterId characterId = playerProfile.getCharacterId(changedCharacter);
		playerProfile.setLastModifiedCharacterId(characterId);
		saveProfile(playerProfile);
	}

	private void saveProfile(PlayerProfile playerProfile) {
		PlayerProfilePO playerProfilePO = playerProfilePOConverter.convert(playerProfile);

		playerProfileRepository.saveProfile(playerProfilePO);
	}

	private PlayerProfile getPlayerProfile(PlayerProfilePO profile) {
		return playerProfilePOConverter.convertBack(profile);
	}
}
