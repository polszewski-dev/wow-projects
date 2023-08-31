package wow.commons.model.item.impl;

import lombok.Getter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.BasicItemInfo;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public abstract class AbstractItemImpl implements AbstractItem {
	private final int id;
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final BasicItemInfo basicItemInfo;

	protected AbstractItemImpl(
			int id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BasicItemInfo basicItemInfo
	) {
		this.id = id;
		this.description = description;
		this.timeRestriction = timeRestriction;
		this.characterRestriction = characterRestriction;
		this.basicItemInfo = basicItemInfo;
	}

	protected void assertItemType(ItemType... itemTypes) {
		if (!Set.of(itemTypes).contains(getItemType())) {
			throw new IllegalArgumentException("Wrong item type: " + getItemType());
		}
	}

	@Override
	public String toString() {
		return getName();
	}
}
