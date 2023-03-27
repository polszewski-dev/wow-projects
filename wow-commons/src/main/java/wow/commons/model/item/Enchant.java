package wow.commons.model.item;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.ConfigurationElementWithAttributes;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface Enchant extends ConfigurationElementWithAttributes<Integer> {
	List<ItemType> getItemTypes();

	ItemRarity getRarity();

	default boolean matches(ItemType itemType) {
		return getItemTypes().contains(itemType);
	}
}
