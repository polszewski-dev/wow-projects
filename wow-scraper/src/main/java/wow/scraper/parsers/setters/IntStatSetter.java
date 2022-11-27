package wow.scraper.parsers.setters;

import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class IntStatSetter implements StatSetter {
	private final PrimitiveAttributeSupplier attributeParser;
	private final int groupNo;

	public IntStatSetter(PrimitiveAttributeSupplier attributeParser, int groupNo) {
		this.attributeParser = attributeParser;
		this.groupNo = groupNo;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		int value = matcher.getInt(groupNo);
		attributeParser.addAttributeList(itemStats, value);
	}
}
