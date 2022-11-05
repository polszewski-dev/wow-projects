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
import wow.minmax.service.UpgradeService;

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

	private final UpgradeService upgradeService;

	@GetMapping
	public List<ItemDTO> getItems() {
		List<Item> result = itemService.getItems();
		return itemConverter.convertList(result);
	}

	@GetMapping(path = "phase/{phase}")
	public List<ItemDTO> getItems(@PathVariable("phase") Phase phase) {
		List<Item> result = itemService.getItems(phase);
		return itemConverter.convertList(result);
	}

	@GetMapping(path = "phase/{phase}/slot/{slot}")
	public List<ItemDTO> getItems(@PathVariable("phase") Phase phase, @PathVariable("slot") ItemSlot slot) {
		List<Item> result = itemService.getItems(phase, slot);
		return itemConverter.convertList(result);
	}

	@GetMapping(path = "phase/{phase}/byslot")
	public Map<ItemSlot, List<ItemDTO>> getItemsBySlot(@PathVariable("phase") Phase phase) {
		CharacterInfo characterInfo = new CharacterInfo(CharacterClass.Warlock, Race.Orc, phase.getGameVersion().getMaxLevel(), List.of());//TODO parametr
		SpellSchool spellSchool = SpellSchool.Shadow;

		var itemsBySlot = itemService.getItemsBySlot(characterInfo, phase, spellSchool);

		return itemsBySlot.entrySet().stream()
					.collect(Collectors.toMap(
							Map.Entry::getKey,
							e -> getItemsDTOsOrderedByScore(e.getValue(), spellSchool)
					)
		);
	}

	private List<ItemDTO> getItemsDTOsOrderedByScore(List<Item> items, SpellSchool spellSchool) {
		return items.stream()
				.map(item -> getItemDTO(item, spellSchool))
				.sorted(orderByScore())
				.collect(Collectors.toList());
	}

	private ItemDTO getItemDTO(Item item, SpellSchool spellSchool) {
		ItemDTO itemDTO = itemConverter.convert(item);
		itemDTO.setScore(upgradeService.getItemScore(item, spellSchool));
		return itemDTO;
	}

	private static Comparator<ItemDTO> orderByScore() {
		return Comparator.comparingDouble((ItemDTO itemDTO) -> -itemDTO.getScore())
				.thenComparing(ItemDTO::getName);
	}
}
