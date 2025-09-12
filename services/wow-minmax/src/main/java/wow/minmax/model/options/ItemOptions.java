package wow.minmax.model.options;

import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record ItemOptions(
		ItemSlot itemSlot,
		List<Item> items
) {
}
