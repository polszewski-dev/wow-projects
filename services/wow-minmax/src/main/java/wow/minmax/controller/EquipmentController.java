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
import wow.minmax.model.PlayerId;
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

	@GetMapping("{playerId}")
	public EquipmentDTO getEquipment(
			@PathVariable("playerId") PlayerId playerId
	) {
		var equipment = equipmentService.getEquipment(playerId);

		return equipmentConverter.convert(equipment);
	}

	@PutMapping("{playerId}/slot/{slot}")
	public List<ItemSlotStatusDTO> equipItem(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("slot") ItemSlot slot,
			@RequestBody EquippableItemDTO itemDTO,
			@RequestParam(name="best-variant", required=false, defaultValue="false") boolean bestVariant,
			@RequestParam Map<String, String> requestParams
	) {
		var item = equippableItemConverter.convertBack(itemDTO, playerId.phaseId());
		var gemFilter = paramToGemFilterConverter.convert(requestParams);

		var changedSlots = equipmentService.equipItem(playerId, slot, item, bestVariant, gemFilter);

		log.info("Equipped item charId: {}, slot: {}, changedSlots: {}", playerId, slot, changedSlots);

		return itemSlotStatusConverter.convertList(changedSlots);
	}

	@PutMapping("{playerId}/slot-group/{slotGroup}")
	public List<ItemSlotStatusDTO> equipItemGroup(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody List<EquippableItemDTO> itemDTOs
	) {
		var items = equippableItemConverter.convertBackList(itemDTOs, playerId.phaseId());

		var changedSlots = equipmentService.equipItemGroup(playerId, slotGroup, items);

		log.info("Equipped items charId: {}, slotGroup: {}, items: {}", playerId, slotGroup, items);

		return itemSlotStatusConverter.convertList(changedSlots);
	}

	@DeleteMapping("{playerId}")
	public void resetEquipment(
			@PathVariable("playerId") PlayerId playerId
	) {
		equipmentService.resetEquipment(playerId);

		log.info("Reset charId: {}", playerId);
	}

	@GetMapping("{playerId}/socket-status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("playerId") PlayerId playerId
	) {
		var equipmentSocketStatus = equipmentService.getEquipmentSocketStatus(playerId);

		return equipmentSocketStatusConverter.convert(equipmentSocketStatus);
	}

	@GetMapping("{playerId}/gear-set")
	public List<String> getAvailableGearSets(
			@PathVariable("playerId") PlayerId playerId
	) {
		var gearSets = equipmentService.getAvailableGearSets(playerId);

		return gearSets.stream()
				.sorted(
						comparing(GearSet::getEarliestPhaseId).reversed()
						.thenComparing(GearSet::getName)
				)
				.map(GearSet::getName)
				.toList();
	}

	@GetMapping("{playerId}/gear-set/{gearSet}/equip")
	public EquipmentDTO equipGearSet(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("gearSet") String gearSet
	) {
		var player = equipmentService.equipGearSet(playerId, gearSet);
		var equipment = player.getEquipment();

		log.info("Equipped gear set: {}, charId: {}", gearSet, playerId);

		return equipmentConverter.convert(equipment);
	}

	@GetMapping("{playerId}/equip-previous-phase")
	public EquipmentDTO equipPreviousPhase(
			@PathVariable("playerId") PlayerId playerId
	) {
		var player = equipmentService.equipPreviousPhase(playerId);
		var equipment = player.getEquipment();

		log.info("Equipped previous phase, charId: {}", playerId);

		return equipmentConverter.convert(equipment);
	}
}
