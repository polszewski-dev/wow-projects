package wow.scraper.parsers.setters;

import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatMatcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class ExpressionStatSetter implements StatSetter {
	@Override
	public void set(AttributesBuilder itemStats, StatMatcher matcher) {
		ComplexAttribute complexAttribute = matcher.getExpression();
		itemStats.addAttribute(complexAttribute);
	}
}
