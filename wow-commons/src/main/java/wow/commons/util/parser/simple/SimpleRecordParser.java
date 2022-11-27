package wow.commons.util.parser.simple;

import java.util.LinkedHashMap;
import java.util.Map;

import static wow.commons.util.parser.simple.SimpleRecordConstants.*;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
class SimpleRecordParser {
	private final String string;
	private String line;
	private int position;

	public SimpleRecordParser(String string) {
		this.string = string;
	}

	public ParseResult parse() {
		ParseResult result = new ParseResult();

		if (string == null || string.isBlank()) {
			result.setMap(Map.of());
			return result;
		}

		int arrowIndex = string.indexOf(ARROW);

		if (arrowIndex < 0) {
			throw new IllegalArgumentException(String.format("Missing '%s' element", ARROW));
		}

		result.setType(parseType(arrowIndex));
		result.setMap(parseMap(arrowIndex));
		return result;
	}

	private String parseType(int arrowIndex) {
		String type = string.substring(0, arrowIndex).trim();
		if (type.isEmpty()) {
			throw new IllegalArgumentException("Type can't be empty");
		}
		return type;
	}

	private Map<String, String> parseMap(int arrowIndex) {
		this.line = string.substring(arrowIndex + ARROW.length()).trim();
		this.position = 0;

		Map<String, String> result = new LinkedHashMap<>();

		while (hasNextChar()) {
			String key = parseKey();
			String value = parseValue();
			assertUniqueKey(key, result);
			result.put(key, value);
		}

		return result;
	}

	private String parseKey() {
		StringBuilder sb = new StringBuilder();

		while (hasNextChar()) {
			char c = nextChar();
			if (c == EQUALS) {
				break;
			}
			sb.append(c);
		}

		String key = sb.toString().trim();

		if (key.isEmpty()) {
			throw new IllegalArgumentException("Key can't be null");
		}

		return key;
	}

	private String parseValue() {
		StringBuilder sb = new StringBuilder();

		while (hasNextChar()) {
			char c = nextChar();
			if (c == COMMA) {
				break;
			}
			if (c == ESCAPE) {
				c = nextChar();
			}
			sb.append(c);
		}

		String value = sb.toString().trim();

		if (value.isEmpty()) {
			return null;
		}

		return value;
	}

	private boolean hasNextChar() {
		return position < line.length();
	}

	private char nextChar() {
		return line.charAt(position++);
	}

	private void assertUniqueKey(String key, Map<String, String> result) {
		if (result.containsKey(key)) {
			throw new IllegalArgumentException(String.format("Duplicate key '%s' in: %s", key, string));
		}
	}
}
