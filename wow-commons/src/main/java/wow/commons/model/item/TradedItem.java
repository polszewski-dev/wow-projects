package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
@Getter
public class TradedItem extends AbstractItem {
	public TradedItem(Integer id, Description description, Restriction restriction, ItemType itemType, BasicItemInfo basicItemInfo) {
		super(id, description, restriction, Attributes.EMPTY, itemType, basicItemInfo);
		if (itemType != ItemType.QUEST && itemType != ItemType.TOKEN) {
			throw new IllegalArgumentException("Wrong item type: " + itemType);
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
