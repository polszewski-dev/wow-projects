package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.dto.ItemFilterConverter;
import wow.minmax.converter.dto.UpgradeConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.dto.ItemFilterDTO;
import wow.minmax.model.dto.UpgradeDTO;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@RestController
@RequestMapping("api/v1/upgrades")
@AllArgsConstructor
@Slf4j
public class UpgradeController {
	private final UpgradeService upgradeService;
	private final PlayerCharacterService playerCharacterService;
	private final UpgradeConverter upgradeConverter;
	private final ItemFilterConverter itemFilterConverter;

	@GetMapping("{characterId}/slot/{slotGroup}")
	public List<UpgradeDTO> findUpgrades(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestParam Map<String, String> requestParams
	) {
		var character = playerCharacterService.getCharacter(characterId).copy();
		var itemFilter = getItemFilter(requestParams);
		var upgrades = upgradeService.findUpgrades(
				character, slotGroup, itemFilterConverter.convertBack(itemFilter)
		);

		return upgrades.stream()
				.map(upgradeConverter::convert)
				.toList();
	}

	private ItemFilterDTO getItemFilter(Map<String, String> requestParams) {
		var result = new ItemFilterDTO();
		requestParams.forEach((key, value) -> {
			switch (key) {
				case "heroics" -> result.setHeroics(parseBoolean(value));
				case "raids" -> result.setRaids(parseBoolean(value));
				case "worldBosses" -> result.setWorldBosses(parseBoolean(value));
				case "pvpItems" -> result.setPvpItems(parseBoolean(value));
				case "greens" -> result.setGreens(parseBoolean(value));
				case "legendaries" -> result.setLegendaries(parseBoolean(value));
				default -> throw new IllegalArgumentException();
			}
		});
		return result;
	}
}
