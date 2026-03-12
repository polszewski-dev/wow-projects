package wow.estimator.client.dto.upgrade;

import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.RaidDTO;
import wow.commons.model.categorization.ItemSlot;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetBestItemVariantRequestDTO(
		RaidDTO raid,
		NonPlayerDTO target,
		int itemId,
		ItemSlot itemSlot,
		GemFilterDTO gemFilter,
		Set<String> enchantNames
) {
}
