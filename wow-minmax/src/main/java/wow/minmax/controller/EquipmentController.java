package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;
import wow.minmax.converter.dto.*;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.dto.*;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.util.AttributeFormatter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final EquipmentConverter equipmentConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@GetMapping("{characterId}")
	public EquipmentDTO getEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerProfileService.getCharacter(characterId);
		return equipmentConverter.convert(character.getEquipment());
	}

	@GetMapping("{characterId}/options")
	public EquipmentOptionsDTO getEquipmentOptions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerProfileService.getCharacter(characterId);
		var itemFilter = ItemFilter.everything();

		var itemOptions = getItemOptions(character, itemFilter);
		var enchantOptions = getEnchantOptions(character, itemOptions);
		var gemOptions = getGemOptions(character);

		var gems = minmaxConfigRepository.hasFeature(character, CharacterFeature.GEMS);
		var heroics = minmaxConfigRepository.hasFeature(character, CharacterFeature.HEROICS);

		return new EquipmentOptionsDTO(itemOptions, enchantOptions, gemOptions, gems, heroics);
	}

	private List<ItemOptionsDTO> getItemOptions(PlayerCharacter character, ItemFilter itemFilter) {
		return ItemSlot.getDpsSlots().stream()
				.map(itemSlot -> new ItemOptionsDTO(itemSlot, itemConverter.convertList(itemService.getItemsBySlot(character, itemSlot, itemFilter))))
				.toList();
	}

	private List<EnchantOptionsDTO> getEnchantOptions(PlayerCharacter character, List<ItemOptionsDTO> itemOptions) {
		var list = createTypeAndSubtypeGroups(itemOptions);

		for (EnchantOptionsDTO x : list) {
			var enchants = itemService.getEnchants(character, x.getItemType(), x.getItemSubType());
			x.setEnchants(enchantConverter.convertList(enchants));
		}

		return list;
	}

	private static List<EnchantOptionsDTO> createTypeAndSubtypeGroups(List<ItemOptionsDTO> itemOptions) {
		var map = new EnumMap<ItemType, Map<ItemSubType, EnchantOptionsDTO>>(ItemType.class);

		itemOptions.stream()
				.flatMap(x -> x.getItems().stream())
				.forEach(item -> map
						.computeIfAbsent(item.getItemType(), x -> new HashMap<>())
						.computeIfAbsent(item.getItemSubType(), x -> new EnchantOptionsDTO(item.getItemType(), item.getItemSubType(), null)));

		return map.values().stream().flatMap(x -> x.values().stream()).toList();
	}

	private List<GemOptionsDTO> getGemOptions(PlayerCharacter character) {
		return Stream.of(SocketType.values())
				.map(socketType -> new GemOptionsDTO(socketType, gemConverter.convertList(itemService.getGems(character, socketType, false))))
				.toList();
	}

	@GetMapping("{characterId}/change/item/{slot}/{itemId}/best/variant")
	public EquippableItemDTO changeItemBestVariant(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@PathVariable("itemId") int itemId
	) {
		var character = playerProfileService.changeItemBestVariant(characterId, slot, itemId);
		log.info("Changed item charId: {}, slot: {}, itemId: {}", characterId, slot, itemId);
		return equippableItemConverter.convert(character.getEquippedItem(slot));
	}

	@PutMapping("{characterId}/change/item/{slot}")
	public EquippableItemDTO changeItem(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot,
			@RequestBody EquippableItemDTO itemDTO
	) {
		var item = getEquippableItem(itemDTO, characterId);
		var character = playerProfileService.changeItem(characterId, slot, item);
		log.info("Changed item charId: {}, slot: {}, item: {}", characterId, slot, item);
		return equippableItemConverter.convert(character.getEquippedItem(slot));
	}

	@PutMapping("{characterId}/change/item/group/{slotGroup}")
	public void changeItemGroup(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody List<EquippableItemDTO> itemDTOs
	) {
		var items = getEquippableItems(characterId, itemDTOs);
		playerProfileService.changeItemGroup(characterId, slotGroup, items);
		log.info("Changed items charId: {}, slotGroup: {}, items: {}", characterId, slotGroup, items);
	}

	private EquippableItem getEquippableItem(EquippableItemDTO itemDTO, CharacterId characterId) {
		var character = playerProfileService.getCharacter(characterId);
		return equippableItemConverter.convertBack(itemDTO, createParams(character.getPhaseId()));
	}

	private List<EquippableItem> getEquippableItems(CharacterId characterId, List<EquippableItemDTO> itemDTOs) {
		var character = playerProfileService.getCharacter(characterId);
		return equippableItemConverter.convertBackList(itemDTOs, createParams(character.getPhaseId()));
	}

	@GetMapping("{characterId}/reset")
	public EquipmentDTO resetEquipment(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerProfileService.resetEquipment(characterId);
		log.info("Reset charId: {}", characterId);
		return equipmentConverter.convert(character.getEquipment());
	}

	@GetMapping("{characterId}/socket/status")
	public EquipmentSocketStatusDTO getEquipmentSocketStatus(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerProfileService.getCharacter(characterId);
		return new EquipmentSocketStatusDTO(ItemSlot.getDpsSlots().stream()
				.collect(Collectors.toMap(x -> x, x -> getItemSocketStatus(character, x)))
		);
	}

	private ItemSocketStatusDTO getItemSocketStatus(PlayerCharacter character, ItemSlot itemSlot) {
		var item = character.getEquippedItem(itemSlot);

		if (item == null) {
			return new ItemSocketStatusDTO(List.of(), new SocketBonusStatusDTO("", false));
		}

		var equipment = character.getEquipment();
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
