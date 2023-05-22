package wow.scraper.parsers.setters;

import lombok.AllArgsConstructor;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.PrimitiveAttributeSupplier;
import wow.commons.util.parser.ParserUtil;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
@AllArgsConstructor
public class IntStatSetter implements StatSetter {
	private final String attributePattern;
	private final int groupNo;
	private final Double constantValue;

	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		String substituted = ParserUtil.substituteParams(attributePattern, matcher::getString);
		PrimitiveAttributeSupplier attributeSupplier = PrimitiveAttributeSupplier.fromString(substituted);
		double value = constantValue != null ? constantValue : matcher.getDouble(groupNo);
		attributeSupplier.addAttributeList(itemStats, value);
	}
}
