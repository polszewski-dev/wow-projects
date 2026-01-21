package wow.commons.model.item;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRoleClassified;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.Effect;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-05
 */
public interface Enchant extends Described, TimeRestricted, CharacterRestricted, PveRoleClassified {
	EnchantId getId();

	List<ItemType> getItemTypes();

	List<ItemSubType> getItemSubTypes();

	int getRequiredItemLevel();

	ItemRarity getRarity();

	int getAppliedEnchantId();

	Effect getEffect();

	default boolean matches(ItemType itemType, ItemSubType itemSubType) {
		return (getItemTypes().isEmpty() || (itemType != null && getItemTypes().contains(itemType))) &&
				(getItemSubTypes().isEmpty() || (itemSubType != null && getItemSubTypes().contains(itemSubType)));
	}
}
