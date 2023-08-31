package wow.scraper.parser.setter;

import wow.commons.util.AttributesBuilder;
import wow.scraper.parser.stat.StatMatcher;

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
