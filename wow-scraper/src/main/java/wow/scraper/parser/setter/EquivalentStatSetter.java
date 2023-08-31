package wow.scraper.parser.setter;

import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.special.SpecialAbility;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parser.stat.StatMatcher;

/**
 * User: POlszewski
 * Date: 2022-01-18
 */
public class EquivalentStatSetter implements StatSetter {
	private final int groupNo;

	public EquivalentStatSetter(int groupNo) {
		this.groupNo = groupNo;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		SpecialAbility equivalent = getSpecialAbility(matcher);
		itemStats.addAttribute(equivalent);
	}

	private SpecialAbility getSpecialAbility(StatMatcher matcher) {
		String line = matcher.getString(groupNo);
		Attributes attributes = matcher.getParamStats();

		return SpecialAbility.equivalent(attributes, line);
	}
}
