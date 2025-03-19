package wow.evaluator.client.dto.upgrade;

import wow.commons.client.dto.ItemDTO;
import wow.commons.client.dto.PlayerDTO;
import wow.commons.model.categorization.ItemSlot;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record GetBestItemVariantRequestDTO(
		PlayerDTO player,
		ItemDTO item,
		ItemSlot itemSlot,
		GemFilterDTO gemFilter,
		Set<String> enchantNames
) {
}
