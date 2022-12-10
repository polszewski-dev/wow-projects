package wow.scraper.parsers.setters;

import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.commons.util.parser.ParserUtil;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class IntStatSetter implements StatSetter {
	private final String attributePattern;
	private final int groupNo;

	public IntStatSetter(String attributePattern, int groupNo) {
		this.attributePattern = attributePattern;
		this.groupNo = groupNo;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		String substituted = ParserUtil.substituteParams(attributePattern, matcher::getString);
		PrimitiveAttributeSupplier attributeSupplier = PrimitiveAttributeSupplier.fromString(substituted);
		double value = matcher.getDouble(groupNo);
		attributeSupplier.addAttributeList(itemStats, value);
	}
}
