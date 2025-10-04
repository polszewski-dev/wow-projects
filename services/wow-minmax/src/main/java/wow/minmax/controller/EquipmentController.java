package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.GearSet;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.client.dto.equipment.EquipmentDTO;
import wow.minmax.client.dto.equipment.EquipmentSocketStatusDTO;
import wow.minmax.client.dto.equipment.EquippableItemDTO;
import wow.minmax.client.dto.equipment.ItemSlotStatusDTO;
import wow.minmax.converter.dto.equipment.*;
import wow.minmax.model.CharacterId;
import wow.minmax.service.EquipmentService;

import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;

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
	private final ItemSlotStatusConverter itemSlotStatusConverter;
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
	public List<ItemSlotStatusDTO> equipItem(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@RequestBody EquippableItemDTO itemDTO,
			@RequestParam(name="best-variant", required=false, defaultValue="false") boolean bestVariant,
			@RequestParam Map<String, String> requestParams
	) {
		var item = equippableItemConverter.convertBack(itemDTO, characterId.phaseId());
		var gemFilter = paramToGemFilterConverter.convert(requestParams);

		var changedSlots = equipmentService.equipItem(characterId, slot, item, bestVariant, gemFilter);

		log.info("Equipped item charId: {}, slot: {}, changedSlots: {}", characterId, slot, changedSlots);

		return itemSlotStatusConverter.convertList(changedSlots);
	}

	@PutMapping("{characterId}/slot-group/{slotGroup}")
	public List<ItemSlotStatusDTO> equipItemGroup(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody List<EquippableItemDTO> itemDTOs
	) {
		var items = equippableItemConverter.convertBackList(itemDTOs, characterId.phaseId());

		var changedSlots = equipmentService.equipItemGroup(characterId, slotGroup, items);

		log.info("Equipped items charId: {}, slotGroup: {}, items: {}", characterId, slotGroup, items);

		return itemSlotStatusConverter.convertList(changedSlots);
	}

	@DeleteMapping("{characterId}")
	public void resetEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		equipmentService.resetEquipment(characterId);

		log.info("Reset charId: {}", characterId);
	}

	@GetMapping("{characterId}/socket-status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("characterId") CharacterId characterId
	) {
		var equipmentSocketStatus = equipmentService.getEquipmentSocketStatus(characterId);

		return equipmentSocketStatusConverter.convert(equipmentSocketStatus);
	}

	@GetMapping("{characterId}/gear-set")
	public List<String> getAvailableGearSets(
			@PathVariable("characterId") CharacterId characterId
	) {
		var gearSets = equipmentService.getAvailableGearSets(characterId);

		return gearSets.stream()
				.sorted(
						comparing(GearSet::getEarliestPhaseId).reversed()
						.thenComparing(GearSet::getName)
				)
				.map(GearSet::getName)
				.toList();
	}

	@GetMapping("{characterId}/gear-set/{gearSet}/equip")
	public EquipmentDTO equipGearSet(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("gearSet") String gearSet
	) {
		var player = equipmentService.equipGearSet(characterId, gearSet);
		var equipment = player.getEquipment();

		log.info("Equipped gear set: {}, charId: {}", gearSet, characterId);

		return equipmentConverter.convert(equipment);
	}

	@GetMapping("{characterId}/equip-previous-phase")
	public EquipmentDTO equipPreviousPhase(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = equipmentService.equipPreviousPhase(characterId);
		var equipment = player.getEquipment();

		log.info("Equipped previous phase, charId: {}", characterId);

		return equipmentConverter.convert(equipment);
	}
}
