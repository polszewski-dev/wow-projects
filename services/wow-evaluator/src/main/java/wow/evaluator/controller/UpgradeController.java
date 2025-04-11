package wow.evaluator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.client.converter.EquippableItemConverter;
import wow.commons.client.converter.ItemConverter;
import wow.evaluator.client.converter.upgrade.GemFilterConverter;
import wow.evaluator.client.converter.upgrade.ItemFilterConverter;
import wow.evaluator.client.converter.upgrade.ItemLevelFilterConverter;
import wow.evaluator.client.dto.upgrade.FindUpgradesRequestDTO;
import wow.evaluator.client.dto.upgrade.FindUpgradesResponseDTO;
import wow.evaluator.client.dto.upgrade.GetBestItemVariantRequestDTO;
import wow.evaluator.client.dto.upgrade.GetBestItemVariantResponseDTO;
import wow.evaluator.converter.PlayerConverter;
import wow.evaluator.converter.UpgradeConverter;
import wow.evaluator.service.UpgradeService;

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
	private final PlayerConverter playerConverter;
	private final UpgradeConverter upgradeConverter;
	private final ItemConverter itemConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final ItemFilterConverter itemFilterConverter;
	private final ItemLevelFilterConverter itemLevelFilterConverter;
	private final GemFilterConverter gemFilterConverter;

	@PostMapping
	public FindUpgradesResponseDTO findUpgrades(@RequestBody FindUpgradesRequestDTO request) {
		var player = playerConverter.convertBack(request.player());
		var slotGroup = request.slotGroup();
		var itemFilter = itemFilterConverter.convertBack(request.itemFilter());
		var itemLevelFilter = itemLevelFilterConverter.convertBack(request.itemLevelFilter());
		var gemFilter = gemFilterConverter.convertBack(request.gemFilter());
		var enchantNames = request.enchantNames();
		var maxUpgrades = request.maxUpgrades();

		var upgrades = upgradeService.findUpgrades(
				player, slotGroup, itemFilter, itemLevelFilter, gemFilter, enchantNames, maxUpgrades
		);

		return new FindUpgradesResponseDTO(upgradeConverter.convertList(upgrades));
	}

	@PostMapping("best-variant")
	public GetBestItemVariantResponseDTO getBestItemVariant(@RequestBody GetBestItemVariantRequestDTO request) {
		var player = playerConverter.convertBack(request.player());
		var item = itemConverter.convertBack(request.item(), player.getPhaseId());
		var itemSlot = request.itemSlot();
		var gemFilter = gemFilterConverter.convertBack(request.gemFilter());
		var enchantNames = request.enchantNames();

		var result = upgradeService.getBestItemVariant(
				player, item, itemSlot, gemFilter, enchantNames
		);

		return new GetBestItemVariantResponseDTO(equippableItemConverter.convert(result));
	}
}
