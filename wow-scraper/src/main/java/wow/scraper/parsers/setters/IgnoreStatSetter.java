package wow.scraper.parsers.setters;

import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class IgnoreStatSetter implements StatSetter {
	public static final IgnoreStatSetter INSTANCE = new IgnoreStatSetter();

	private IgnoreStatSetter() {}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		// do nothing
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
