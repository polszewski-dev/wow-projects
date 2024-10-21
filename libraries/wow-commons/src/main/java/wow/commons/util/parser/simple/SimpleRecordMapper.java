package wow.commons.util.parser.simple;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public final class SimpleRecordMapper {
	public static String toString(String type, Map<String, ?> fields) {
		return new SimpleRecordSerializer().serialize(type, fields);
	}

	public static ParseResult fromString(String value) {
		return new SimpleRecordParser(value).parse();
	}

	private SimpleRecordMapper() {}
}
