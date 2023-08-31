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
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@RestController
@RequestMapping("api/v1/upgrade")
@AllArgsConstructor
@Slf4j
public class UpgradeController {
	private final UpgradeService upgradeService;
	private final PlayerProfileService playerProfileService;
	private final UpgradeConverter upgradeConverter;
	private final ItemFilterConverter itemFilterConverter;

	@PostMapping("{characterId}/slot/{slotGroup}")
	public List<UpgradeDTO> findUpgrades(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestBody ItemFilterDTO itemFilter
	) {
		var character = playerProfileService.getCharacter(characterId).copy();
		var upgrades = upgradeService.findUpgrades(
				character, slotGroup, itemFilterConverter.convertBack(itemFilter)
		);

		return upgrades.stream()
				.map(upgradeConverter::convert)
				.toList();
	}
}
