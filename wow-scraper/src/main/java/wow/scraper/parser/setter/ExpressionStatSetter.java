package wow.scraper.parser.setter;

import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parser.stat.StatMatcher;

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
