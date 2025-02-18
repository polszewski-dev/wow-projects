package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.equipment.ItemFilter;
import wow.commons.client.converter.EnchantConverter;
import wow.commons.client.converter.GemConverter;
import wow.commons.client.converter.ItemConverter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;
import wow.minmax.client.dto.EnchantOptionsDTO;
import wow.minmax.client.dto.EquipmentOptionsDTO;
import wow.minmax.client.dto.GemOptionsDTO;
import wow.minmax.client.dto.ItemOptionsDTO;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.ItemService;
import wow.minmax.service.PlayerCharacterService;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2024-04-17
 */
@RestController
@RequestMapping("api/v1/equipment-options")
@AllArgsConstructor
@Slf4j
public class EquipmentOptionsController {
	private final PlayerCharacterService playerCharacterService;
	private final ItemService itemService;
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@GetMapping("{characterId}")
	public EquipmentOptionsDTO getEquipmentOptions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);

		var gems = minmaxConfigRepository.hasFeature(player, CharacterFeature.GEMS);
		var heroics = minmaxConfigRepository.hasFeature(player, CharacterFeature.HEROICS);

		return new EquipmentOptionsDTO(gems, heroics);
	}

	@GetMapping("{characterId}/item/{slot}")
	public ItemOptionsDTO getItemOptions(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slot") ItemSlot slot
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var itemFilter = ItemFilter.everything();

		return getItemOptions(player, slot, itemFilter);
	}

	@GetMapping("{characterId}/enchant")
	public List<EnchantOptionsDTO> getEnchantOptions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var itemFilter = ItemFilter.everything();

		var itemOptions = getItemOptions(player, itemFilter);

		return getEnchantOptions(player, itemOptions);
	}

	@GetMapping("{characterId}/gem")
	public List<GemOptionsDTO> getGemOptions(
			@PathVariable("characterId") CharacterId characterId
	) {
		var player = playerCharacterService.getPlayer(characterId);

		return getGemOptions(player);
	}

	private List<ItemOptionsDTO> getItemOptions(Player player, ItemFilter itemFilter) {
		return ItemSlot.getDpsSlots().stream()
				.map(itemSlot -> getItemOptions(player, itemSlot, itemFilter))
				.toList();
	}

	private ItemOptionsDTO getItemOptions(Player player, ItemSlot itemSlot, ItemFilter itemFilter) {
		var items = itemService.getItemsBySlot(player, itemSlot, itemFilter);
		var itemDTOs = itemConverter.convertList(items);

		return new ItemOptionsDTO(itemSlot, itemDTOs);
	}


	private List<EnchantOptionsDTO> getEnchantOptions(Player player, List<ItemOptionsDTO> itemOptions) {
		return createTypeAndSubtypeGroups(itemOptions).stream()
				.map(x -> attachEnchants(player, x))
				.toList();
	}

	private static List<EnchantOptionsDTO> createTypeAndSubtypeGroups(List<ItemOptionsDTO> itemOptions) {
		var map = new EnumMap<ItemType, Map<ItemSubType, EnchantOptionsDTO>>(ItemType.class);

		itemOptions.stream()
				.flatMap(x -> x.items().stream())
				.forEach(item -> map
						.computeIfAbsent(item.itemType(), x -> new HashMap<>())
						.computeIfAbsent(item.itemSubType(), x -> new EnchantOptionsDTO(item.itemType(), item.itemSubType(), null)));

		return map.values().stream().flatMap(x -> x.values().stream()).toList();
	}

	private EnchantOptionsDTO attachEnchants(Player player, EnchantOptionsDTO dto) {
		var enchants = itemService.getEnchants(player, dto.itemType(), dto.itemSubType());

		return dto.withEnchants(enchantConverter.convertList(enchants));
	}

	private List<GemOptionsDTO> getGemOptions(Player player) {
		return Stream.of(SocketType.values())
				.map(socketType -> getGemOptions(player, socketType))
				.toList();
	}

	private GemOptionsDTO getGemOptions(Player player, SocketType socketType) {
		var gems = itemService.getGems(player, socketType);
		var gemDTOs = gemConverter.convertList(gems);

		return new GemOptionsDTO(socketType, gemDTOs);
	}
}
