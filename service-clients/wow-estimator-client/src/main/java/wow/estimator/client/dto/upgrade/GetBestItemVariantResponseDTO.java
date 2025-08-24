package wow.estimator.client.dto.upgrade;

import wow.commons.client.dto.EquippableItemDTO;

/**
 * User: POlszewski
 * Date: 2025-03-18
 */
public record GetBestItemVariantResponseDTO(
		EquippableItemDTO bestVariant
) {
}
