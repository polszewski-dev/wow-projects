package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.repository.impl.parsers.stats.StatMatcher;
import wow.commons.util.AttributesBuilder;

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

		Integer amount = matcher.getParamAmount();
		Attributes attributes = matcher.getParamStats(amount);

		return SpecialAbility.equivalent(attributes, line);
	}
}
