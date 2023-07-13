package wow.commons.model.item;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.config.ConfigurationElementWithAttributes;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface Enchant extends ConfigurationElementWithAttributes<Integer>, PveRoleClassified {
	List<ItemType> getItemTypes();

	List<ItemSubType> getItemSubTypes();

	ItemRarity getRarity();

	default boolean matches(ItemType itemType, ItemSubType itemSubType) {
		return (getItemTypes().isEmpty() || (itemType != null && getItemTypes().contains(itemType))) &&
				(getItemSubTypes().isEmpty() || (itemSubType != null && getItemSubTypes().contains(itemSubType)));
	}
}
