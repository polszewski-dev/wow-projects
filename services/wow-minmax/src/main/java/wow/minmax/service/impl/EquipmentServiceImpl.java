package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.GearSet;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.service.CharacterService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.effect.Effect;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.model.equipment.*;
import wow.minmax.service.EquipmentService;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

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
	public List<ItemSlotStatus> equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item) {
		return equipItem(characterId, slot, item, false, GemFilter.empty());
	}

	@Override
	public List<ItemSlotStatus> equipItem(CharacterId characterId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter) {
		var player = playerCharacterService.getPlayer(characterId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, gemFilter, player);

		var oldEquipment = player.getEquipment().deepCopy();

		characterService.equipItem(player, slot, itemToEquip);

		playerCharacterService.saveCharacter(characterId, player);

		return getEquipmentDiff(oldEquipment, player);
	}

	@Override
	public List<ItemSlotStatus> equipItemGroup(CharacterId characterId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		var player = playerCharacterService.getPlayer(characterId);

		var oldEquipment = player.getEquipment().deepCopy();

		characterService.equipItemGroup(player, slotGroup, items);

		playerCharacterService.saveCharacter(characterId, player);

		return getEquipmentDiff(oldEquipment, player);
	}

	private List<ItemSlotStatus> getEquipmentDiff(Equipment oldEquipment, Player player) {
		var newEquipment = player.getEquipment().deepCopy();
		var changedSlots = newEquipment.getChangedSlots(oldEquipment);

		return changedSlots.entrySet().stream()
				.map(x -> new ItemSlotStatus(x.getKey(), x.getValue()))
				.toList();
	}

	@Override
	public Player resetEquipment(CharacterId characterId) {
		var player = playerCharacterService.getPlayer(characterId);

		player.resetEquipment();

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	private EquippableItem getItemToEquip(ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter, Player player) {
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

	private ItemSocketStatus getItemSocketStatus(Player player, ItemSlot itemSlot) {
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
	public Player equipGearSet(CharacterId characterId, String gearSet) {
		var player = playerCharacterService.getPlayer(characterId);

		characterService.equipGearSet(player, gearSet);

		playerCharacterService.saveCharacter(characterId, player);

		return player;
	}

	@Override
	public Player equipPreviousPhase(CharacterId characterId) {
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
