package wow.scraper.parsers.setters;

import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public interface StatSetter {
	void set(AttributesBuilder itemStats, StatMatcher matcher);

	default boolean isEmpty() {
		return false;
	}
}
