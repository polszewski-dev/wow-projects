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
	public TradedItem(Integer id, Description description, Restriction restriction, BasicItemInfo basicItemInfo) {
		super(id, description, restriction, Attributes.EMPTY, basicItemInfo);
		if (getItemType() != ItemType.QUEST && getItemType() != ItemType.TOKEN) {
			throw new IllegalArgumentException("Wrong item type: " + getItemType());
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
