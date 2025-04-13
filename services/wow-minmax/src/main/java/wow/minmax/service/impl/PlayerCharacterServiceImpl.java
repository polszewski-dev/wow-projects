package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.service.CharacterService;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.persistent.PlayerCharacterPOConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.impl.NonPlayerImpl;
import wow.minmax.model.impl.PlayerImpl;
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
	public Player getPlayer(CharacterId characterId) {
		return getExistingOrNewCharacter(characterId);
	}

	private Player getExistingOrNewCharacter(CharacterId characterId) {
		return playerCharacterRepository.findById(characterId.toString())
				.map(playerCharacterPOConverter::convertBack)
				.orElseGet(() -> createCharacter(characterId));
	}

	private Player createCharacter(CharacterId characterId) {
		var profileId = characterId.getProfileId().toString();
		var playerProfile = playerProfileRepository.findById(profileId).orElseThrow();

		var newCharacter = characterService.createPlayerCharacter(
				playerProfile.getProfileName(),
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.getLevel(),
				characterId.getPhaseId(),
				PlayerImpl::new
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				"Target",
				characterId.getEnemyType(),
				newCharacter.getLevel() + characterId.getEnemyLevelDiff(),
				characterId.getPhaseId(),
				NonPlayerImpl::new
		);

		newCharacter.setTarget(targetEnemy);

		characterService.applyDefaultCharacterTemplate(newCharacter);

		return newCharacter;
	}

	@Override
	public Player equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item) {
		return equipItem(characterId, slot, item, false, GemFilter.empty());
	}

	@Override
	public Player equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter) {
		var player = getPlayer(characterId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, gemFilter, player);

		player.equip(itemToEquip, slot);
		saveCharacter(characterId, player);
		return player;
	}

	private void saveCharacter(CharacterId characterId, Player player) {
		var characterEntity = playerCharacterPOConverter.convert(player, characterId);

		playerCharacterRepository.save(characterEntity);

		var profile = playerProfileRepository.findById(characterId.getProfileId().toString()).orElseThrow();

		profile.setLastModifiedCharacterId(characterId.toString());
		profile.setLastModified(LocalDateTime.now());

		playerProfileRepository.save(profile);
	}

	private EquippableItem getItemToEquip(ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter, Player player) {
		if (bestVariant) {
			return upgradeService.getBestItemVariant(player, item.getItem(), slot, gemFilter);
		} else {
			return item;
		}
	}

	@Override
	public Player equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
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
	public Player resetEquipment(CharacterId characterId) {
		var player = getPlayer(characterId);

		player.resetEquipment();
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public Player enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled) {
		var player = getPlayer(characterId);

		player.getBuffList(buffListType).enable(buffId, rank, enabled);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public Player enableConsumable(CharacterId characterId, String consumableName, boolean enabled) {
		var player = getPlayer(characterId);

		player.getConsumables().enable(consumableName, enabled);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public ViewConfig getViewConfig(Player player) {
		return minmaxConfigRepository.getViewConfig(player).orElseThrow();
	}
}
