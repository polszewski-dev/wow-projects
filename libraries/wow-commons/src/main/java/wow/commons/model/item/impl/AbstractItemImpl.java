package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.AbstractItemId;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.pve.PhaseId;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public abstract class AbstractItemImpl<T extends AbstractItemId> implements AbstractItem<T> {
	private final T id;
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final BasicItemInfo basicItemInfo;
	@Setter
	private PhaseId firstAppearedInPhase;

	protected AbstractItemImpl(
			T id,
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
