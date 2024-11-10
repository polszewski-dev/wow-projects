package wow.minmax.client.dto;

import wow.commons.client.dto.ItemDTO;
import wow.commons.model.categorization.ItemSlot;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
public record ItemOptionsDTO(
		ItemSlot itemSlot,
		List<ItemDTO> items
) {
}
