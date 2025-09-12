package wow.commons.model.item.impl;

import lombok.Getter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.TradedItem;
import wow.commons.model.item.TradedItemId;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class TradedItemImpl extends AbstractItemImpl<TradedItemId> implements TradedItem {
	public TradedItemImpl(
			TradedItemId id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BasicItemInfo basicItemInfo
	) {
		super(id, description, timeRestriction, characterRestriction, basicItemInfo);
		assertItemType(ItemType.QUEST, ItemType.TOKEN);
	}
}
