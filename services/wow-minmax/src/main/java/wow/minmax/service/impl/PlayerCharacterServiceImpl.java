package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.service.CharacterService;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.persistent.PlayerCharacterPOConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerCharacterRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;

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
	public PlayerCharacter getPlayer(CharacterId characterId) {
		return getExistingOrNewCharacter(characterId);
	}

	private PlayerCharacter getExistingOrNewCharacter(CharacterId characterId) {
		return playerCharacterRepository.findById(characterId.toString())
				.map(playerCharacterPOConverter::convertBack)
				.orElseGet(() -> createCharacter(characterId));
	}

	private PlayerCharacter createCharacter(CharacterId characterId) {
		var profileId = characterId.profileId().toString();
		var playerProfile = playerProfileRepository.findById(profileId).orElseThrow();

		var newCharacter = characterService.createPlayerCharacter(
				playerProfile.getProfileName(),
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.level(),
				characterId.phaseId(),
				PlayerCharacterImpl::new
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				"Target",
				characterId.enemyType(),
				newCharacter.getLevel() + characterId.enemyLevelDiff(),
				characterId.phaseId(),
				NonPlayerCharacterImpl::new
		);

		newCharacter.setTarget(targetEnemy);

		characterService.applyDefaultCharacterTemplate(newCharacter);

		return newCharacter;
	}

	@Override
	public PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item) {
		return equipItem(characterId, slot, item, false, GemFilter.empty());
	}

	@Override
	public PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter) {
		var player = getPlayer(characterId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, gemFilter, player);

		player.equip(itemToEquip, slot);
		saveCharacter(characterId, player);
		return player;
	}

	private void saveCharacter(CharacterId characterId, PlayerCharacter player) {
		var characterEntity = playerCharacterPOConverter.convert(player, characterId);

		playerCharacterRepository.save(characterEntity);

		var profile = playerProfileRepository.findById(characterId.profileId().toString()).orElseThrow();

		profile.setLastModifiedCharacterId(characterId.toString());
		profile.setLastModified(LocalDateTime.now());

		playerProfileRepository.save(profile);
	}

	private EquippableItem getItemToEquip(ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter, PlayerCharacter player) {
		if (bestVariant) {
			return upgradeService.getBestItemVariant(player, item.getItem(), slot, gemFilter);
		} else {
			return item;
		}
	}

	@Override
	public PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		var player = getPlayer(characterId);
		var slots = slotGroup.getSlots();

		for (var slot : slots) {
			player.equip(null, slot);
		}

		if (slotGroup == ItemSlotGroup.WEAPONS && items.size() == 1) {
			items = Arrays.asList(items.getFirst(), null);
		}

		for (int slotIdx = 0; slotIdx < min(slots.size(), items.size()); slotIdx++) {
			var slot = slots.get(slotIdx);
			var item = items.get(slotIdx);
			player.equip(item, slot);
		}

		saveCharacter(characterId, player);

		return player;
	}

	@Override
	public PlayerCharacter resetEquipment(CharacterId characterId) {
		var player = getPlayer(characterId);

		player.resetEquipment();
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled) {
		var player = getPlayer(characterId);

		player.getBuffList(buffListType).enable(buffId, rank, enabled);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public PlayerCharacter enableConsumable(CharacterId characterId, String consumableName, boolean enabled) {
		var player = getPlayer(characterId);

		player.getConsumables().enable(consumableName, enabled);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public ViewConfig getViewConfig(PlayerCharacter player) {
		return minmaxConfigRepository.getViewConfig(player).orElseThrow();
	}
}
