package wow.commons.model.item.impl;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.*;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class ItemImpl extends AbstractItemImpl implements Item {
	private ItemSet itemSet;
	private final ItemSocketSpecification socketSpecification;
	private final WeaponStats weaponStats;

	public ItemImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Attributes attributes,
			BasicItemInfo basicItemInfo,
			ItemSocketSpecification socketSpecification,
			WeaponStats weaponStats
	) {
		super(id, description, timeRestriction, characterRestriction, attributes, basicItemInfo);
		this.socketSpecification = socketSpecification;
		this.weaponStats = weaponStats;
	}

	public void setItemSet(ItemSet itemSet) {
		this.itemSet = itemSet;
	}
}
