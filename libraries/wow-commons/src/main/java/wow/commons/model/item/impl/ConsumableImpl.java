package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.BasicItemInfo;
import wow.commons.model.item.Consumable;
import wow.commons.model.spell.ActivatedAbility;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
@Getter
@Setter
public class ConsumableImpl extends AbstractItemImpl implements Consumable {
	private ActivatedAbility activatedAbility;

	public ConsumableImpl(
			int id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BasicItemInfo basicItemInfo
	) {
		super(id, description, timeRestriction, characterRestriction, basicItemInfo);
		assertItemType(ItemType.CONSUMABLE);
	}
}
