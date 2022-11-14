package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UpgradeController {
	private final UpgradeService upgradeService;
	private final PlayerProfileService playerProfileService;
	private final UpgradeConverter upgradeConverter;

	@GetMapping("{profileId}/slot/{slotGroup}")
	public List<UpgradeDTO> findUpgrades(
			@PathVariable("profileId") UUID profileId,
			@PathVariable("slotGroup") ItemSlotGroup slotGroup
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId).copy();
		List<Comparison> upgrades = upgradeService.findUpgrades(playerProfile, slotGroup, playerProfile.getDamagingSpellId());

		return upgrades.stream()
				.map(upgradeConverter::convert)
				.limit(20)
				.collect(Collectors.toList());
	}

	@GetMapping("{profileId}")
	public UpgradesDTO findUpgrades(
			@PathVariable("profileId") UUID profileId
	) {
		long start = System.currentTimeMillis();

		try {
			return new UpgradesDTO(
					findUpgrades(profileId, ItemSlotGroup.HEAD),
					findUpgrades(profileId, ItemSlotGroup.NECK),
					findUpgrades(profileId, ItemSlotGroup.SHOULDER),
					findUpgrades(profileId, ItemSlotGroup.BACK),
					findUpgrades(profileId, ItemSlotGroup.CHEST),
					findUpgrades(profileId, ItemSlotGroup.WRIST),
					findUpgrades(profileId, ItemSlotGroup.HANDS),
					findUpgrades(profileId, ItemSlotGroup.WAIST),
					findUpgrades(profileId, ItemSlotGroup.LEGS),
					findUpgrades(profileId, ItemSlotGroup.FEET),
					findUpgrades(profileId, ItemSlotGroup.FINGERS),
					findUpgrades(profileId, ItemSlotGroup.TRINKETS),
					findUpgrades(profileId, ItemSlotGroup.WEAPONS),
					findUpgrades(profileId, ItemSlotGroup.RANGED)
			);
		} finally {
			log.info("It took {} millis.", System.currentTimeMillis() - start);
		}
	}
}
