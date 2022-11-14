package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CharacterInfo;
import wow.commons.model.unit.Race;
import wow.minmax.converter.dto.ItemConverter;
import wow.minmax.model.dto.ItemDTO;
import wow.minmax.service.ItemService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@RestController
@RequestMapping("api/v1/item")
@AllArgsConstructor
public class ItemController {
	private final ItemService itemService;
	private final ItemConverter itemConverter;

	@GetMapping
	public List<ItemDTO> getItems() {
		List<Item> result = itemService.getItems();
		return itemConverter.convertList(result);
	}

	@GetMapping("phase/{phase}")
	public List<ItemDTO> getItems(@PathVariable("phase") Phase phase) {
		List<Item> result = itemService.getItems(phase);
		return itemConverter.convertList(result);
	}

	@GetMapping("phase/{phase}/slot/{slot}")
	public List<ItemDTO> getItems(@PathVariable("phase") Phase phase, @PathVariable("slot") ItemSlot slot) {
		List<Item> result = itemService.getItems(phase, slot);
		return itemConverter.convertList(result);
	}

	@GetMapping("phase/{phase}/byslot")
	public Map<ItemSlot, List<ItemDTO>> getItemsBySlot(@PathVariable("phase") Phase phase) {
		CharacterInfo characterInfo = new CharacterInfo(CharacterClass.WARLOCK, Race.ORC, phase.getGameVersion().getMaxLevel(), List.of());//TODO parametr
		SpellSchool spellSchool = SpellSchool.SHADOW;

		var itemsBySlot = itemService.getItemsBySlot(characterInfo, phase, spellSchool);

		return itemsBySlot.entrySet().stream()
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							e -> getItemsDTOsOrderedByScore(e.getValue())
					)
		);
	}

	private List<ItemDTO> getItemsDTOsOrderedByScore(List<Item> items) {
		return items.stream()
				.map(this::getItemDTO)
				.sorted(Comparator.comparing(ItemDTO::getScore).reversed().thenComparing(ItemDTO::getName))
				.collect(Collectors.toList());
	}

	private ItemDTO getItemDTO(Item item) {
		ItemDTO itemDTO = itemConverter.convert(item);
		itemDTO.setScore(item.getItemLevel());
		return itemDTO;
	}
}
