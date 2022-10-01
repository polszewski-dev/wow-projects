package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.minmax.converter.dto.ItemConverter;
import wow.minmax.model.dto.ItemDTO;
import wow.minmax.service.ItemService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

	@GetMapping(path = "phase/{phase}")
	public List<ItemDTO> getItems(@PathVariable("phase") int phase) {
		List<Item> result = itemService.getItems(phase);
		return itemConverter.convertList(result);
	}

	@GetMapping(path = "phase/{phase}/slot/{slot}")
	public List<ItemDTO> getItems(@PathVariable("phase") int phase, @PathVariable("slot") ItemSlot slot) {
		List<Item> result = itemService.getItems(phase, slot);
		return itemConverter.convertList(result);
	}

	@GetMapping(path = "phase/{phase}/byslot")
	public Map<ItemSlot, List<ItemDTO>> getItemsBySlot(@PathVariable("phase") int phase) {
		CharacterClass characterClass = CharacterClass.Warlock;
		SpellSchool spellSchool = SpellSchool.Shadow;
		var itemsBySlot = itemService.getItemsBySlot(phase, characterClass, spellSchool);
		var result = itemConverter.convertGroups(itemsBySlot);

		for (List<ItemDTO> list : result.values()) {
			sortItems(list);
		}

		return result;
	}

	private void sortItems(List<ItemDTO> list) {
		list.sort(Comparator.comparingDouble((ItemDTO itemDTO) -> -itemDTO.getScore())
							.thenComparing(ItemDTO::getName));
	}
}
