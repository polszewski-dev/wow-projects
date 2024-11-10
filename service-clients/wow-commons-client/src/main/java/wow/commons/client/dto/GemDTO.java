package wow.commons.client.dto;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.GemColor;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public record GemDTO(
		int id,
		String name,
		GemColor color,
		ItemRarity rarity,
		String source,
		String shortName,
		String icon,
		String tooltip
) {
}
