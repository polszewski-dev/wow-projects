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

import java.util.*;
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
		List<Comparison> upgrades = upgradeService.findUpgrades(playerProfile, slotGroup, playerProfile.getDamagingSpell());

		return toUpgradeDTOs(upgrades);
	}

	private List<UpgradeDTO> toUpgradeDTOs(List<Comparison> upgrades) {
		return upgrades.stream()
				.map(upgradeConverter::convert)
				.collect(Collectors.toList());
	}

	@GetMapping("{profileId}")
	public UpgradesDTO findUpgrades(
			@PathVariable("profileId") UUID profileId
	) {
		long start = System.currentTimeMillis();

		try {
			PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId).copy();
			var result = new EnumMap<ItemSlotGroup, List<Comparison>>(ItemSlotGroup.class);

			for (ItemSlotGroup slotGroup : ItemSlotGroup.values()) {
				if (!IGNORED_SLOT_GROUPS.contains(slotGroup)) {
					List<Comparison> upgrades = upgradeService.findUpgrades(playerProfile, slotGroup, playerProfile.getDamagingSpell());
					result.put(slotGroup, upgrades);
				}
			}

			return new UpgradesDTO(
				result.entrySet().stream()
						.collect(Collectors.toMap(
								Map.Entry::getKey,
								e -> toUpgradeDTOs(e.getValue())
						))
			);
		} finally {
			log.info("It took {} millis.", System.currentTimeMillis() - start);
		}
	}

	private static final Set<ItemSlotGroup> IGNORED_SLOT_GROUPS = Set.of(
			ItemSlotGroup.TABARD,
			ItemSlotGroup.SHIRT,
			ItemSlotGroup.FINGER_1,
			ItemSlotGroup.FINGER_2,
			ItemSlotGroup.TRINKET_1,
			ItemSlotGroup.TRINKET_2,
			ItemSlotGroup.MAIN_HAND,
			ItemSlotGroup.OFF_HAND
	);
}
