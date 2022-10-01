package wow.commons.repository.impl.parsers.setters;

import wow.commons.model.attributes.Attribute;
import wow.commons.repository.impl.parsers.SimpleAttributeParser;
import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class IntStatSetter implements StatSetter {
	private final SimpleAttributeParser attributeParser;

	IntStatSetter(SimpleAttributeParser attributeParser) {
		this.attributeParser = attributeParser;
	}

	@Override
	public void set(AttributesBuilder itemStats, StatParser parser, int groupNo) {
		int value = parser.get(groupNo);
		for (Attribute attribute : attributeParser.getAttributes(value)) {
			itemStats.addAttribute(attribute);
		}
	}
}
