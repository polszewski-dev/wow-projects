package wow.commons.repository.impl.parsers.setters;

import wow.commons.repository.impl.parsers.SimpleAttributeParser;
import wow.commons.repository.impl.parsers.StatParser;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class ParsedStatSetter {
	private final int groupNo;
	private final StatSetter setter;

	private ParsedStatSetter(int groupNo, SimpleAttributeParser attributeParser) {
		this(groupNo, new IntStatSetter(attributeParser));
	}

	private ParsedStatSetter(int groupNo, StatSetter setter) {
		this.groupNo = groupNo;
		this.setter = setter;
	}

	public static ParsedStatSetter group(int groupNo, SimpleAttributeParser attributeParser) {
		return new ParsedStatSetter(groupNo, attributeParser);
	}

	public static ParsedStatSetter group(int groupNo, StatSetter statSetter) {
		return new ParsedStatSetter(groupNo, statSetter);
	}

	public void setStats(AttributesBuilder itemStats, StatParser parser) {
		setter.set(itemStats, parser, groupNo);
	}

	public boolean hasAnySetter() {
		return !(setter instanceof IgnoreStatSetter);
	}
}
