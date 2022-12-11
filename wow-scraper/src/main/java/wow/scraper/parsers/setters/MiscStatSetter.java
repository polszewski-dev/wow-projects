package wow.scraper.parsers.setters;

import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class MiscStatSetter implements StatSetter {
	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		SpecialAbility miscAbility = SpecialAbility.misc(matcher.getString(0));
		itemStats.addAttribute(miscAbility);
	}
}
