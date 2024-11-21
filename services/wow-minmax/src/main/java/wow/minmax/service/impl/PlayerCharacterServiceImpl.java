package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.CharacterService;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.persistent.PlayerCharacterPOConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.impl.NonPlayerCharacterImpl;
import wow.minmax.model.impl.PlayerCharacterImpl;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerCharacterRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
@Service
@AllArgsConstructor
public class PlayerCharacterServiceImpl implements PlayerCharacterService {
	private final UpgradeService upgradeService;
	private final CharacterService characterService;
	private final MinmaxConfigRepository minmaxConfigRepository;

	private final PlayerCharacterRepository playerCharacterRepository;
	private final PlayerCharacterPOConverter playerCharacterPOConverter;

	private final PlayerProfileRepository playerProfileRepository;

	@Override
	public PlayerCharacter getCharacter(CharacterId characterId) {
		return getExistingOrNewCharacter(characterId);
	}

	private PlayerCharacter getExistingOrNewCharacter(CharacterId characterId) {
		return playerCharacterRepository.findById(characterId.toString())
				.map(playerCharacterPOConverter::convertBack)
				.orElseGet(() -> createCharacter(characterId));
	}

	private PlayerCharacter createCharacter(CharacterId characterId) {
		var profileId = characterId.getProfileId().toString();
		var playerProfile = playerProfileRepository.findById(profileId).orElseThrow();

		var newCharacter = characterService.createPlayerCharacter(
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.getLevel(),
				characterId.getPhaseId(),
				PlayerCharacterImpl::new
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				characterId.getEnemyType(),
				newCharacter.getLevel() + characterId.getEnemyLevelDiff(),
				characterId.getPhaseId(),
				NonPlayerCharacterImpl::new
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
		var character = getCharacter(characterId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, character);

		character.equip(itemToEquip, slot);
		saveCharacter(characterId, character);
		return character;
	}

	private void saveCharacter(CharacterId characterId, PlayerCharacter character) {
		var characterEntity = playerCharacterPOConverter.convert(character, characterId);

		playerCharacterRepository.save(characterEntity);

		var profile = playerProfileRepository.findById(characterId.getProfileId().toString()).orElseThrow();

		profile.setLastModifiedCharacterId(characterId.toString());
		profile.setLastModified(LocalDateTime.now());

		playerProfileRepository.save(profile);
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
		var character = getCharacter(characterId);
		var slots = slotGroup.getSlots();

		for (var slot : slots) {
			character.equip(null, slot);
		}

		if (slotGroup == ItemSlotGroup.WEAPONS && items.size() == 1) {
			items = Arrays.asList(items.getFirst(), null);
		}

		for (int slotIdx = 0; slotIdx < slots.size(); slotIdx++) {
			var slot = slots.get(slotIdx);
			var item = items.get(slotIdx);
			character.equip(item, slot);
		}

		saveCharacter(characterId, character);

		return character;
	}

	@Override
	public PlayerCharacter resetEquipment(CharacterId characterId) {
		var character = getCharacter(characterId);

		character.resetEquipment();
		saveCharacter(characterId, character);
		return character;
	}

	@Override
	public PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled) {
		var character = getCharacter(characterId);

		character.getBuffList(buffListType).enable(buffId, rank, enabled);
		saveCharacter(characterId, character);
		return character;
	}

	@Override
	public PlayerCharacter enableConsumable(CharacterId characterId, String consumableName, boolean enabled) {
		var character = getCharacter(characterId);

		character.getConsumables().enable(consumableName, enabled);
		saveCharacter(characterId, character);
		return character;
	}

	@Override
	public ViewConfig getViewConfig(PlayerCharacter character) {
		return minmaxConfigRepository.getViewConfig(character).orElseThrow();
	}
}
