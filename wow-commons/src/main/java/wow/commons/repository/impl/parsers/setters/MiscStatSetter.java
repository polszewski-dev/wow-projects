package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.repository.impl.parsers.stats.StatMatcher;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class MiscStatSetter implements StatSetter {
	private final int groupNo;

	public MiscStatSetter(int groupNo) {
		this.groupNo = groupNo;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		SpecialAbility miscAbility = SpecialAbility.misc(matcher.getString(groupNo));
		itemStats.addAttribute(miscAbility);
	}
}
