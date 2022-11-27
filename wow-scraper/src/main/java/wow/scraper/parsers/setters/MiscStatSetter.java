package wow.scraper.parsers.setters;

import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

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
