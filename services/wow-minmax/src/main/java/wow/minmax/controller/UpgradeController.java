package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.evaluator.client.dto.upgrade.UpgradeDTO;
import wow.minmax.converter.dto.ParamToGemFilterConverter;
import wow.minmax.converter.dto.ParamToItemFilterConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.service.PlayerCharacterService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.Map;

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
	private final ParamToItemFilterConverter paramToItemFilterConverter;
	private final ParamToGemFilterConverter paramToGemFilterConverter;

	@GetMapping("{characterId}/slot/{slotGroup}")
	public List<UpgradeDTO> findUpgrades(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup,
			@RequestParam Map<String, String> requestParams
	) {
		var player = playerCharacterService.getPlayer(characterId);
		var itemFilter = paramToItemFilterConverter.convert(requestParams);
		var gemFilter = paramToGemFilterConverter.convert(requestParams);

		return upgradeService.findUpgrades(player, slotGroup, itemFilter, gemFilter).upgrades();
	}
}
