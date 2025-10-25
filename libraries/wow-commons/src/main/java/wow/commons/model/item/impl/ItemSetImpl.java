package wow.commons.model.item.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.profession.ProfessionId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@RequiredArgsConstructor
@Getter
public class ItemSetImpl implements ItemSet {
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private final List<String> pieces;
	private final ProfessionId requiredProfession;
	@Setter
	private List<ItemSetBonus> itemSetBonuses;

	@Override
	public String toString() {
		return getName();
	}
}
