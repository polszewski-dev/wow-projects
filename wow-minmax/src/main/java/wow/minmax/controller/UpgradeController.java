package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.minmax.converter.dto.UpgradeConverter;
import wow.minmax.model.Comparison;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.dto.UpgradeDTO;
import wow.minmax.model.dto.UpgradesDTO;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.UpgradeService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@RestController
@RequestMapping("api/v1/upgrade")
@AllArgsConstructor
public class UpgradeController {
	private final UpgradeService upgradeService;
	private final PlayerProfileService playerProfileService;
	private final UpgradeConverter upgradeConverter;

	@GetMapping(path = "{profileId}/slot/{slotGroup}")
	public List<UpgradeDTO> findUpgrades(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId).readOnlyCopy();
		List<Comparison> upgrades = upgradeService.findUpgrades(playerProfile, slotGroup, playerProfile.getDamagingSpellId());

		return upgrades.stream()
				.map(upgradeConverter::convert)
				.limit(20)
				.collect(Collectors.toList());
	}

	@GetMapping(path = "{profileId}")
	public UpgradesDTO findUpgrades(
			@PathVariable("profileId") UUID profileId
	) {
		long start = System.currentTimeMillis();

		try {
			return new UpgradesDTO(
					findUpgrades(profileId, ItemSlotGroup.Head),
					findUpgrades(profileId, ItemSlotGroup.Neck),
					findUpgrades(profileId, ItemSlotGroup.Shoulder),
					findUpgrades(profileId, ItemSlotGroup.Back),
					findUpgrades(profileId, ItemSlotGroup.Chest),
					findUpgrades(profileId, ItemSlotGroup.Wrist),
					findUpgrades(profileId, ItemSlotGroup.Hands),
					findUpgrades(profileId, ItemSlotGroup.Waist),
					findUpgrades(profileId, ItemSlotGroup.Legs),
					findUpgrades(profileId, ItemSlotGroup.Feet),
					findUpgrades(profileId, ItemSlotGroup.Fingers),
					findUpgrades(profileId, ItemSlotGroup.Trinkets),
					findUpgrades(profileId, ItemSlotGroup.Weapons),
					findUpgrades(profileId, ItemSlotGroup.Ranged)
			);
		} finally {
			System.out.println("It took " + (System.currentTimeMillis() - start) + " millis.");
		}
	}
}
