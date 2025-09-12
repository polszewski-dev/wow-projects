package wow.commons.client.dto.equipment;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-02
 */
public record EquippableItemDTO(
		int itemId,
		Integer enchantId,
		List<Integer> gemIds
) {
}
