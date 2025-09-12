package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.client.dto.equipment.EquipmentDTO;
import wow.minmax.client.dto.equipment.EquipmentSocketStatusDTO;
import wow.minmax.client.dto.equipment.EquippableItemDTO;
import wow.minmax.converter.dto.equipment.EquipmentConverter;
import wow.minmax.converter.dto.equipment.EquipmentSocketStatusConverter;
import wow.minmax.converter.dto.equipment.EquippableItemConverter;
import wow.minmax.converter.dto.equipment.ParamToGemFilterConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.service.EquipmentService;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@RestController
@RequestMapping("api/v1/equipments")
@AllArgsConstructor
@Slf4j
public class EquipmentController {
	private final EquipmentService equipmentService;
	private final EquipmentConverter equipmentConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final EquipmentSocketStatusConverter equipmentSocketStatusConverter;
	private final ParamToGemFilterConverter paramToGemFilterConverter;

	@GetMapping("{characterId}")
	public EquipmentDTO getEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		var equipment = equipmentService.getEquipment(characterId);

		return equipmentConverter.convert(equipment);
	}

	@PutMapping("{characterId}/slot/{slot}")
	public EquippableItemDTO equipItem(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@RequestBody EquippableItemDTO itemDTO,
			@RequestParam(name="best-variant", required=false, defaultValue="false") boolean bestVariant,
			@RequestParam Map<String, String> requestParams
	) {
		var item = equippableItemConverter.convertBack(itemDTO, characterId.phaseId());
		var gemFilter = paramToGemFilterConverter.convert(requestParams);

		var player = equipmentService.equipItem(characterId, slot, item, bestVariant, gemFilter);
		var equippedItem = player.getEquippedItem(slot);

		log.info("Equipped item charId: {}, slot: {}, item: {}", characterId, slot, equippedItem);
		return equippableItemConverter.convert(equippedItem);
	}

	@PutMapping("{characterId}/slot-group/{slotGroup}")
	public void equipItemGroup(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody List<EquippableItemDTO> itemDTOs
	) {
		var items = equippableItemConverter.convertBackList(itemDTOs, characterId.phaseId());

		equipmentService.equipItemGroup(characterId, slotGroup, items);

		log.info("Equipped items charId: {}, slotGroup: {}, items: {}", characterId, slotGroup, items);
	}

	@DeleteMapping("{characterId}")
	public EquipmentDTO resetEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = equipmentService.resetEquipment(characterId);

		log.info("Reset charId: {}", characterId);

		return equipmentConverter.convert(player.getEquipment());
	}

	@GetMapping("{characterId}/socket-status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("characterId") CharacterId characterId
	) {
		var equipmentSocketStatus = equipmentService.getEquipmentSocketStatus(characterId);

		return equipmentSocketStatusConverter.convert(equipmentSocketStatus);
	}
}
