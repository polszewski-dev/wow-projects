package wow.commons.client.dto.equipment;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public record EquippableItemDTO(
		ItemDTO item,
		EnchantDTO enchant,
		List<GemDTO> gems
) {
}
