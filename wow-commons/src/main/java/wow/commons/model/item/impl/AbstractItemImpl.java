package wow.commons.model.item.impl;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.BasicItemInfo;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public abstract class AbstractItemImpl extends ConfigurationElementWithAttributesImpl<Integer> implements AbstractItem {
	private final BasicItemInfo basicItemInfo;

	protected AbstractItemImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Attributes attributes,
			BasicItemInfo basicItemInfo
	) {
		super(id, description, timeRestriction, characterRestriction, attributes);
		this.basicItemInfo = basicItemInfo;
	}

	protected void assertItemType(ItemType... itemTypes) {
		if (!Set.of(itemTypes).contains(getItemType())) {
			throw new IllegalArgumentException("Wrong item type: " + getItemType());
		}
	}
}
