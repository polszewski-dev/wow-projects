package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.converter.EquipmentConverter;
import wow.commons.client.converter.EquippableItemConverter;
import wow.commons.client.dto.EquipmentDTO;
import wow.commons.client.dto.EquippableItemDTO;
import wow.commons.client.util.AttributeFormatter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.client.dto.EquipmentSocketStatusDTO;
import wow.minmax.client.dto.ItemSocketStatusDTO;
import wow.minmax.client.dto.SocketBonusStatusDTO;
import wow.minmax.client.dto.SocketStatusDTO;
import wow.minmax.converter.dto.ParamToGemFilterConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.service.PlayerCharacterService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@RestController
@RequestMapping("api/v1/equipments")
@AllArgsConstructor
@Slf4j
public class EquipmentController {
	private final PlayerCharacterService playerCharacterService;
	private final EquipmentConverter equipmentConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final ParamToGemFilterConverter paramToGemFilterConverter;

	@GetMapping("{characterId}")
	public EquipmentDTO getEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		return equipmentConverter.convert(player.getEquipment());
	}

	@PutMapping("{characterId}/slot/{slot}")
	public EquippableItemDTO equipItem(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@RequestBody EquippableItemDTO itemDTO,
			@RequestParam(name="best-variant", required=false, defaultValue="false") boolean bestVariant,
			@RequestParam Map<String, String> requestParams
	) {
		var item = getEquippableItem(itemDTO, characterId);
		var gemFilter = paramToGemFilterConverter.convert(requestParams);
		var character = playerCharacterService.equipItem(characterId, slot, item, bestVariant, gemFilter);

		var equippedItem = character.getEquippedItem(slot);

		log.info("Equipped item charId: {}, slot: {}, item: {}", characterId, slot, equippedItem);
		return equippableItemConverter.convert(equippedItem);
	}

	@PutMapping("{characterId}/slot-group/{slotGroup}")
	public void equipItemGroup(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody List<EquippableItemDTO> itemDTOs
	) {
		var items = getEquippableItems(itemDTOs, characterId);
		playerCharacterService.equipItemGroup(characterId, slotGroup, items);
		log.info("Equipped items charId: {}, slotGroup: {}, items: {}", characterId, slotGroup, items);
	}

	private EquippableItem getEquippableItem(EquippableItemDTO itemDTO, CharacterId characterId) {
		return equippableItemConverter.convertBack(itemDTO, characterId.getPhaseId());
	}

	private List<EquippableItem> getEquippableItems(List<EquippableItemDTO> itemDTOs, CharacterId characterId) {
		return equippableItemConverter.convertBackList(itemDTOs, characterId.getPhaseId());
	}

	@DeleteMapping("{characterId}")
	public EquipmentDTO resetEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerCharacterService.resetEquipment(characterId);
		log.info("Reset charId: {}", characterId);
		return equipmentConverter.convert(character.getEquipment());
	}

	@GetMapping("{characterId}/socket-status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		return new EquipmentSocketStatusDTO(ItemSlot.getDpsSlots().stream()
				.collect(Collectors.toMap(x -> x, x -> getItemSocketStatus(player, x)))
		);
	}

	private ItemSocketStatusDTO getItemSocketStatus(Player player, ItemSlot itemSlot) {
		var item = player.getEquippedItem(itemSlot);

		if (item == null) {
			return new ItemSocketStatusDTO(List.of(), new SocketBonusStatusDTO("", false));
		}

		var equipment = player.getEquipment();
		var socketStatuses = getSocketStatuses(item, equipment);
		var socketBonusStatus = getSocketBonusStatus(item, equipment);

		return new ItemSocketStatusDTO(socketStatuses, socketBonusStatus);
	}

	private SocketBonusStatusDTO getSocketBonusStatus(EquippableItem item, Equipment equipment) {
		return new SocketBonusStatusDTO(
				getSocketBonusString(item),
				equipment.allSocketsHaveMatchingGems(item)
		);
	}

	private List<SocketStatusDTO> getSocketStatuses(EquippableItem item, Equipment equipment) {
		return Stream.iterate(0, i -> i < item.getSocketCount(), i -> i + 1)
				.map(socketNo -> getSocketStatusDTO(item, equipment, socketNo))
				.toList();
	}

	private SocketStatusDTO getSocketStatusDTO(EquippableItem item, Equipment equipment, Integer socketNo) {
		return new SocketStatusDTO(
				socketNo,
				item.getSocketType(socketNo),
				equipment.hasMatchingGem(item, socketNo)
		);
	}

	private String getSocketBonusString(EquippableItem item) {
		return item.getSocketBonus().getModifierAttributeList().stream()
				.map(AttributeFormatter::format)
				.collect(Collectors.joining(", "));
	}
}
