package wow.estimator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.client.converter.equipment.EquippableItemConverter;
import wow.commons.model.item.ItemId;
import wow.commons.repository.item.ItemRepository;
import wow.estimator.client.converter.upgrade.GemFilterConverter;
import wow.estimator.client.converter.upgrade.ItemFilterConverter;
import wow.estimator.client.converter.upgrade.ItemLevelFilterConverter;
import wow.estimator.client.dto.upgrade.FindUpgradesRequestDTO;
import wow.estimator.client.dto.upgrade.FindUpgradesResponseDTO;
import wow.estimator.client.dto.upgrade.GetBestItemVariantRequestDTO;
import wow.estimator.client.dto.upgrade.GetBestItemVariantResponseDTO;
import wow.estimator.converter.NonPlayerConverter;
import wow.estimator.converter.RaidConverter;
import wow.estimator.converter.UpgradeConverter;
import wow.estimator.service.PlayerService;
import wow.estimator.service.UpgradeService;

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
	private final PlayerService playerService;
	private final ItemRepository itemRepository;
	private final RaidConverter raidConverter;
	private final NonPlayerConverter nonPlayerConverter;
	private final UpgradeConverter upgradeConverter;
	private final EquippableItemConverter equippableItemConverter;
	private final ItemFilterConverter itemFilterConverter;
	private final ItemLevelFilterConverter itemLevelFilterConverter;
	private final GemFilterConverter gemFilterConverter;

	@PostMapping
	public FindUpgradesResponseDTO findUpgrades(@RequestBody FindUpgradesRequestDTO request) {
		var raid = raidConverter.convertBack(request.raid());
		var target = nonPlayerConverter.convertBack(request.target());
		var player = playerService.getPlayer(raid, target);
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
		var raid = raidConverter.convertBack(request.raid());
		var target = nonPlayerConverter.convertBack(request.target());
		var player = playerService.getPlayer(raid, target);
		var itemId = ItemId.of(request.itemId());
		var item = itemRepository.getItem(itemId, player.getPhaseId()).orElseThrow();
		var itemSlot = request.itemSlot();
		var gemFilter = gemFilterConverter.convertBack(request.gemFilter());
		var enchantNames = request.enchantNames();

		var result = upgradeService.getBestItemVariant(
				player, item, itemSlot, gemFilter, enchantNames
		);

		return new GetBestItemVariantResponseDTO(equippableItemConverter.convert(result));
	}
}
