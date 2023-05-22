package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.character.model.character.Character;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.SocketType;
import wow.minmax.converter.dto.*;
import wow.minmax.model.CharacterId;
import wow.minmax.model.dto.*;
import wow.minmax.service.PlayerProfileService;

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
		ItemFilter itemFilter = ItemFilter.everything();

		var itemOptions = getItemOptions(character, itemFilter);
		var enchantOptions = getEnchantOptions(character, itemOptions);
		var gemOptions = getGemOptions(character);

		boolean gems = character.getGameVersion().isGems();
		boolean heroics = character.getGameVersion().isHeroics();

		return new EquipmentOptionsDTO(itemOptions, enchantOptions, gemOptions, gems, heroics);
	}

	private List<ItemOptionsDTO> getItemOptions(Character character, ItemFilter itemFilter) {
		return ItemSlot.getDpsSlots().stream()
				.map(itemSlot -> new ItemOptionsDTO(itemSlot, itemConverter.convertList(itemService.getItemsBySlot(character, itemSlot, itemFilter))))
				.toList();
	}

	private List<EnchantOptionsDTO> getEnchantOptions(Character character, List<ItemOptionsDTO> itemOptions) {
		List<EnchantOptionsDTO> list = createTypeAndSubtypeGroups(itemOptions);

		for (EnchantOptionsDTO x : list) {
			List<Enchant> enchants = itemService.getEnchants(character, x.getItemType(), x.getItemSubType());
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

	private List<GemOptionsDTO> getGemOptions(Character character) {
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
