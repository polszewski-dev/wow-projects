package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.Character;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;
import wow.minmax.converter.dto.*;
import wow.minmax.model.CharacterId;
import wow.minmax.model.dto.*;
import wow.minmax.service.PlayerProfileService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.minmax.converter.dto.DtoConverterParams.createParams;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@RestController
@RequestMapping("api/v1/equipment")
@AllArgsConstructor
@Slf4j
public class EquipmentController {
	private final PlayerProfileService playerProfileService;
	private final ItemService itemService;
	private final EquipmentConverter equipmentConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@GetMapping("{characterId}")
	public EquipmentDTO getEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);
		return equipmentConverter.convert(character.getEquipment());
	}

	@GetMapping("{characterId}/options")
	public EquipmentOptionsDTO getEquipmentOptions(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);

		var itemsByItemSlot = ItemSlot.getDpsSlots().stream()
				.collect(Collectors.toMap(x -> x, x -> itemConverter.convertList(itemService.getItemsBySlot(character, x))));
		var enchantsByItemType = Stream.of(ItemType.values())
				.collect(Collectors.toMap(x -> x, x -> enchantConverter.convertList(itemService.getEnchants(character, x))));
		var gemsBySocketType = Stream.of(SocketType.values())
				.collect(Collectors.toMap(x -> x, x -> gemConverter.convertList(itemService.getGems(character, x, false))));

		boolean gems = character.getGameVersion().isGems();

		return new EquipmentOptionsDTO(itemsByItemSlot, enchantsByItemType, gemsBySocketType, gems);
	}

	@GetMapping("{characterId}/change/item/{slot}/{itemId}/best/variant")
	public EquippableItemDTO changeItemBestVariant(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("itemId") int itemId
	) {
		Character character = playerProfileService.changeItemBestVariant(characterId, slot, itemId);
		log.info("Changed item charId: {}, slot: {}, itemId: {}", characterId, slot, itemId);
		return equippableItemConverter.convert(character.getEquippedItem(slot));
	}

	@PutMapping("{characterId}/change/item/{slot}")
	public EquippableItemDTO changeItem(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@RequestBody EquippableItemDTO itemDTO
	) {
		EquippableItem item = getEquippableItem(itemDTO, characterId);
		Character character = playerProfileService.changeItem(characterId, slot, item);
		log.info("Changed item charId: {}, slot: {}, item: {}", characterId, slot, item);
		return equippableItemConverter.convert(character.getEquippedItem(slot));
	}

	@PutMapping("{characterId}/change/item/group/{slotGroup}")
	public void changeItemGroup(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody List<EquippableItemDTO> itemDTOs
	) {
		List<EquippableItem> items = getEquippableItems(characterId, itemDTOs);
		playerProfileService.changeItemGroup(characterId, slotGroup, items);
		log.info("Changed items charId: {}, slotGroup: {}, items: {}", characterId, slotGroup, items);
	}

	private EquippableItem getEquippableItem(EquippableItemDTO itemDTO, CharacterId characterId) {
		Character character = playerProfileService.getCharacter(characterId);
		return equippableItemConverter.convertBack(itemDTO, createParams(character.getPhaseId()));
	}

	private List<EquippableItem> getEquippableItems(CharacterId characterId, List<EquippableItemDTO> itemDTOs) {
		Character character = playerProfileService.getCharacter(characterId);
		return equippableItemConverter.convertBackList(itemDTOs, createParams(character.getPhaseId()));
	}

	@GetMapping("{characterId}/reset")
	public EquipmentDTO resetEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.resetEquipment(characterId);
		log.info("Reset charId: {}", characterId);
		return equipmentConverter.convert(character.getEquipment());
	}

	@GetMapping("{characterId}/socket/status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);
		return new EquipmentSocketStatusDTO(ItemSlot.getDpsSlots().stream()
				.collect(Collectors.toMap(x -> x, x -> getItemSocketStatus(character, x)))
		);
	}

	private ItemSocketStatusDTO getItemSocketStatus(Character character, ItemSlot itemSlot) {
		EquippableItem item = character.getEquippedItem(itemSlot);

		if (item == null) {
			return new ItemSocketStatusDTO(List.of(), new SocketBonusStatusDTO("", false));
		}

		Equipment equipment = character.getEquipment();

		var socketStatuses = Stream.iterate(0, i -> i < item.getSocketCount(), i -> i + 1)
				.map(socketNo -> new SocketStatusDTO(
						item.getSocketType(socketNo),
						equipment.hasMatchingGem(item, socketNo)
				)).toList();

		var socketBonusStatus = new SocketBonusStatusDTO(
				item.getSocketBonus().statString(),
				equipment.allSocketsHaveMatchingGems(item)
		);

		return new ItemSocketStatusDTO(socketStatuses, socketBonusStatus);
	}
}
