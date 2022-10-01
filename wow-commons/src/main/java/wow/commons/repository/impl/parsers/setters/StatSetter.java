package wow.commons.repository.impl.parsers.setters;

import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public interface StatSetter {
	void set(AttributesBuilder itemStats, StatParser parser, int groupNo);
}
