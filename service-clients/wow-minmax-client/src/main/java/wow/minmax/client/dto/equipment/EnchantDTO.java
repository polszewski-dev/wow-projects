package wow.minmax.client.dto.equipment;

import wow.commons.model.categorization.ItemRarity;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public record EnchantDTO(
		int id,
		String name,
		ItemRarity rarity,
		String icon,
		String tooltip
) {
}
