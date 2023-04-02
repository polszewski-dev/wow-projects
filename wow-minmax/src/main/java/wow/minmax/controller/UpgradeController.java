package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.Character;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.dto.UpgradeConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Comparison;
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

	@GetMapping("{characterId}/slot/{slotGroup}")
	public List<UpgradeDTO> findUpgrades(
			@PathVariable("characterId") CharacterId characterId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup
	) {
		Character character = playerProfileService.getCharacter(characterId).copy();
		List<Comparison> upgrades = upgradeService.findUpgrades(character, slotGroup, character.getDamagingSpell());

		return upgrades.stream()
				.map(upgradeConverter::convert)
				.toList();
	}
}
