package wow.commons.client.dto;

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
