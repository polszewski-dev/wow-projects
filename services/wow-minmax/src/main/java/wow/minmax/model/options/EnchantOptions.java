package wow.minmax.model.options;

import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
public record EnchantOptions(
		ItemType itemType,
		ItemSubType itemSubType,
		List<Enchant> enchants
) {
}
