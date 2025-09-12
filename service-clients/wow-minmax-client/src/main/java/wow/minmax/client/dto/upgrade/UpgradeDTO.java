package wow.minmax.client.dto.upgrade;

import wow.commons.client.dto.equipment.EquippableItemDTO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-02
 */
public record UpgradeDTO(
		 double changePct,
		 List<EquippableItemDTO> itemDifference,
		 List<String> statDifference,
		 List<String> addedAbilities,
		 List<String> removedAbilities
) {
}
