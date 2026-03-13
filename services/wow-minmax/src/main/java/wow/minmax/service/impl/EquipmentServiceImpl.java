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
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.equipment.*;
import wow.minmax.service.EquipmentService;
import wow.minmax.service.PlayerService;
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
	private final PlayerService playerService;
	private final CharacterService characterService;
	private final UpgradeService upgradeService;

	@Override
	public Equipment getEquipment(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);

		return player.getEquipment();
	}

	@Override
	public List<ItemSlotStatus> equipItem(PlayerId playerId, ItemSlot slot, EquippableItem item) {
		return equipItem(playerId, slot, item, false, GemFilter.empty());
	}

	@Override
	public List<ItemSlotStatus> equipItem(PlayerId playerId, ItemSlot slot, EquippableItem item, boolean bestVariant, GemFilter gemFilter) {
		var player = playerService.getPlayer(playerId);
		var itemToEquip = getItemToEquip(slot, item, bestVariant, gemFilter, player);

		var oldEquipment = player.getEquipment().deepCopy();

		characterService.equipItem(player, slot, itemToEquip);

		playerService.savePlayer(player);

		return getEquipmentDiff(oldEquipment, player);
	}

	@Override
	public List<ItemSlotStatus> equipItemGroup(PlayerId playerId, ItemSlotGroup slotGroup, List<EquippableItem> items) {
		var player = playerService.getPlayer(playerId);

		var oldEquipment = player.getEquipment().deepCopy();

		characterService.equipItemGroup(player, slotGroup, items);

		playerService.savePlayer(player);

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
	public Player resetEquipment(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);

		player.resetEquipment();

		playerService.savePlayer(player);

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
	public EquipmentSocketStatus getEquipmentSocketStatus(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);
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
	public List<GearSet> getAvailableGearSets(PlayerId playerId) {
		var player = playerService.getPlayer(playerId);

		return characterService.getAvailableGearSets(player);
	}

	@Override
	public Player equipGearSet(PlayerId playerId, String gearSet) {
		var player = playerService.getPlayer(playerId);

		characterService.equipGearSet(player, gearSet);

		playerService.savePlayer(player);

		return player;
	}

	@Override
	public Player equipPreviousPhase(PlayerId playerId) {
		var previousPhasePlayerId = playerId.getPreviousPhasePlayerId();
		var previousPhasePlayer = playerService.getPlayer(previousPhasePlayerId);
		var player = playerService.getPlayer(playerId);

		player.resetEquipment();

		for (var itemSlot : ItemSlot.values()) {
			var item = previousPhasePlayer.getEquippedItem(itemSlot);

			player.equip(item, itemSlot);
		}

		playerService.savePlayer(player);

		return player;
	}

}
