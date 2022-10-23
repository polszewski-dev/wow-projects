package wow.commons.repository.impl.parsers.setters;

import wow.commons.repository.impl.parsers.stats.PrimitiveAttributeSupplier;
import wow.commons.repository.impl.parsers.stats.StatMatcher;
import wow.commons.util.AttributesBuilder;

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
		itemStats.addAttributeList(attributeParser.getAttributeList(value));
	}
}
