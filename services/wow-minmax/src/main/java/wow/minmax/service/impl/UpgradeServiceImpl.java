package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.client.converter.EquippableItemConverter;
import wow.commons.client.converter.ItemConverter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.item.Item;
import wow.estimator.client.converter.upgrade.GemFilterConverter;
import wow.estimator.client.converter.upgrade.ItemFilterConverter;
import wow.estimator.client.converter.upgrade.ItemLevelFilterConverter;
import wow.estimator.client.dto.upgrade.FindUpgradesRequestDTO;
import wow.estimator.client.dto.upgrade.FindUpgradesResponseDTO;
import wow.estimator.client.dto.upgrade.GetBestItemVariantRequestDTO;
import wow.estimator.client.dto.upgrade.GetBestItemVariantResponseDTO;
import wow.minmax.config.UpgradeConfig;
import wow.minmax.converter.dto.PlayerConverter;
import wow.minmax.model.Player;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.UpgradeService;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class UpgradeServiceImpl implements UpgradeService {
	private final MinmaxConfigRepository minmaxConfigRepository;

	private final PlayerConverter playerConverter;
	private final ItemFilterConverter itemFilterConverter;
	private final ItemLevelFilterConverter itemLevelFilterConverter;
	private final GemFilterConverter gemFilterConverter;
	private final ItemConverter itemConverter;
	private final EquippableItemConverter equippableItemConverter;

	private final UpgradeConfig upgradeConfig;

	@Qualifier("upgradesWebClient")
	private final WebClient webClient;

	@Override
	public FindUpgradesResponseDTO findUpgrades(Player player, ItemSlotGroup slotGroup, ItemFilter itemFilter, GemFilter gemFilter) {
		var findUpgradesConfig = minmaxConfigRepository.getFindUpgradesConfig(player).orElseThrow();
		var itemLevelFilter = minmaxConfigRepository.getItemLevelFilter(player).orElseThrow();

		var request = new FindUpgradesRequestDTO(
				playerConverter.convert(player),
				slotGroup,
				itemFilterConverter.convert(itemFilter),
				itemLevelFilterConverter.convert(itemLevelFilter),
				gemFilterConverter.convert(gemFilter),
				findUpgradesConfig.enchantNames(),
				upgradeConfig.getMaxUpgrades()
		);

		return webClient
				.post()
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(FindUpgradesResponseDTO.class)
				.block();
	}

	@Override
	public EquippableItem getBestItemVariant(Player player, Item item, ItemSlot slot, GemFilter gemFilter) {
		var findUpgradesConfig = minmaxConfigRepository.getFindUpgradesConfig(player).orElseThrow();

		var request = new GetBestItemVariantRequestDTO(
				playerConverter.convert(player),
				itemConverter.convert(item),
				slot,
				gemFilterConverter.convert(gemFilter),
				findUpgradesConfig.enchantNames()
		);

		var response = webClient
				.post()
				.uri("/best-variant")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.retrieve()
				.bodyToMono(GetBestItemVariantResponseDTO.class)
				.block();

		return equippableItemConverter.convertBack(response.bestVariant(), player.getPhaseId());
	}
}
