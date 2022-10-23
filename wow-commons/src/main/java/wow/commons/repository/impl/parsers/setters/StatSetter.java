package wow.commons.repository.impl.parsers.setters;

import wow.commons.repository.impl.parsers.stats.StatMatcher;
import wow.commons.util.AttributesBuilder;

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
