package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.minmax.converter.dto.ItemConverter;
import wow.minmax.model.dto.ItemDTO;
import wow.minmax.service.ItemService;

import java.util.List;

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
}
