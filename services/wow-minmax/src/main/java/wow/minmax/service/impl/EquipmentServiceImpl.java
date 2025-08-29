package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.model.CharacterId;
import wow.minmax.service.EquipmentService;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
@Service
@AllArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {
	private final PlayerCharacterService playerCharacterService;
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
}
