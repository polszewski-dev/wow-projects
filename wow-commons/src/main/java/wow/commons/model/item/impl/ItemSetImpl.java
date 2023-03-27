package wow.commons.model.item.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.item.Tier;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "name")
public class ItemSetImpl implements ItemSet {
	private final String name;
	private final Tier tier;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final List<Item> pieces;
	private List<ItemSetBonus> itemSetBonuses;

	public void setItemSetBonuses(List<ItemSetBonus> itemSetBonuses) {
		this.itemSetBonuses = itemSetBonuses;
	}
}
