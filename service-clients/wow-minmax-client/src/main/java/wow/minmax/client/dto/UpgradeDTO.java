package wow.minmax.client.dto;

import wow.commons.client.dto.EquippableItemDTO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-22
 */
public record UpgradeDTO(
		double changePct,
		List<EquippableItemDTO> itemDifference,
		List<String> statDifference,
		List<String> addedAbilities,
		List<String> removedAbilities
) {
}
