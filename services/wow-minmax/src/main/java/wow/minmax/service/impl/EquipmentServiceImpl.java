package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.GearSet;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.service.CharacterService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.effect.Effect;
import wow.minmax.model.CharacterId;
import wow.minmax.model.equipment.EquipmentSocketStatus;
import wow.minmax.model.equipment.ItemSocketStatus;
import wow.minmax.model.equipment.SocketBonusStatus;
import wow.minmax.model.equipment.SocketStatus;
import wow.minmax.service.EquipmentService;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.toMap;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
@Service
@AllArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
	private final PlayerCharacterService playerCharacterService;
	private final CharacterService characterService;
	private final UpgradeService upgradeService;

	@Override
	public Equipment getEquipment(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		return player.getEquipment();
	}

	@Override
	public PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item) {
		return equipItem(characterId, slot, item, false, GemFilter.empty());
	}

	@Override
	public PlayerCharacter equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter) {
		var player = playerCharacterService.getPlayer(characterId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, gemFilter, player);

		player.equip(itemToEquip, slot);

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	@Override
	public PlayerCharacter equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		var player = playerCharacterService.getPlayer(characterId);
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

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	@Override
	public PlayerCharacter resetEquipment(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		player.resetEquipment();

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	private EquippableItem getItemToEquip(ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter, PlayerCharacter player) {
		if (bestVariant) {
			return upgradeService.getBestItemVariant(player, item.getItem(), slot, gemFilter);
		} else {
			return item;
		}
	}

	@Override
	public EquipmentSocketStatus getEquipmentSocketStatus(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);
		var socketStatusesByItemSlot = ItemSlot.getDpsSlots().stream()
				.collect(
						toMap(
								Function.identity(),
								itemSlot -> getItemSocketStatus(player, itemSlot)
						)
				);

		return new EquipmentSocketStatus(
				socketStatusesByItemSlot
		);
	}

	private ItemSocketStatus getItemSocketStatus(PlayerCharacter player, ItemSlot itemSlot) {
		var item = player.getEquippedItem(itemSlot);

		if (item == null) {
			return NO_ITEM_SOCKET_STATUS;
		}

		var equipment = player.getEquipment();
		var socketStatuses = getSocketStatuses(item, equipment);
		var socketBonusStatus = getSocketBonusStatus(item, equipment);

		return new ItemSocketStatus(socketStatuses, socketBonusStatus);
	}

	private static final ItemSocketStatus NO_ITEM_SOCKET_STATUS = new ItemSocketStatus(
			List.of(),
			new SocketBonusStatus(Effect.EMPTY, false)
	);

	private SocketBonusStatus getSocketBonusStatus(EquippableItem item, Equipment equipment) {
		return new SocketBonusStatus(
				item.getSocketBonus(),
				equipment.allSocketsHaveMatchingGems(item)
		);
	}

	private List<SocketStatus> getSocketStatuses(EquippableItem item, Equipment equipment) {
		return IntStream.range(0, item.getSocketCount())
				.mapToObj(socketNo -> getSocketStatus(item, equipment, socketNo))
				.toList();
	}

	private SocketStatus getSocketStatus(EquippableItem item, Equipment equipment, int socketNo) {
		return new SocketStatus(
				socketNo,
				item.getSocketType(socketNo),
				equipment.hasMatchingGem(item, socketNo)
		);
	}

	@Override
	public List<GearSet> getAvailableGearSets(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		return characterService.getAvailableGearSets(player);
	}

	@Override
	public PlayerCharacter equipGearSet(CharacterId characterId, String gearSet) {
		var player = playerCharacterService.getPlayer(characterId);

		characterService.equipGearSet(player, gearSet);

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	@Override
	public PlayerCharacter equipPreviousPhase(CharacterId characterId) {
		var previousPhaseCharacterId = getPreviousPhaseCharacterId(characterId);
		var previousPhasePlayer = playerCharacterService.getPlayer(previousPhaseCharacterId);
		var player = playerCharacterService.getPlayer(characterId);

		player.resetEquipment();

		for (var itemSlot : ItemSlot.values()) {
			var item = previousPhasePlayer.getEquippedItem(itemSlot);

			player.equip(item, itemSlot);
		}

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	private CharacterId getPreviousPhaseCharacterId(CharacterId characterId) {
		return new CharacterId(
				characterId.profileId(),
				characterId.phaseId().getPreviousPhase().orElseThrow(),
				characterId.level(),
				characterId.enemyType(),
				characterId.enemyLevelDiff()
		);
	}
}
