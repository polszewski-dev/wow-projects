package wow.commons.util.parser.simple;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.util.FormatUtil;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static wow.commons.util.parser.simple.SimpleRecordConstants.*;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
class SimpleRecordSerializer {
	public String serialize(String type, Map<String, ?> fields) {
		StringBuilder sb = new StringBuilder();
		if (type != null) {
			sb.append(type).append(" " + ARROW + " ");
		}
		sb.append(fields.entrySet().stream()
				.map(this::keyValuePair)
				.filter(Objects::nonNull)
				.collect(Collectors.joining(COMMA + " "))
		);
		return sb.toString();
	}

	private String keyValuePair(Map.Entry<String, ?> e) {
		String key = serializeKey(e.getKey());
		String value = serializeValue(e.getValue());
		if (value == null || value.isEmpty()) {
			return null;
		}
		return key + EQUALS + value;
	}

	private String serializeKey(String key) {
		assertKeyIsCorrect(key);
		return key.replace(String.valueOf(EQUALS), String.valueOf(ESCAPE) + EQUALS);
	}

	private String serializeValue(Object value) {
		if (value == null) {
			return null;
		}
		return mapToString(value)
				.trim()
				.replace(String.valueOf(ESCAPE), String.valueOf(ESCAPE) + ESCAPE)
				.replace(String.valueOf(COMMA), String.valueOf(ESCAPE) + COMMA);
	}

	private static String mapToString(Object value) {
		if (value instanceof Double v) {
			return prettyFormat(v);
		}
		if (value instanceof Percent v) {
			return prettyFormat(v.value());
		}
		if (value instanceof Duration v) {
			return v.isInfinite() ? v.toString() : prettyFormat(v.getSeconds());
		}
		return value.toString();
	}

	private static String prettyFormat(Double value) {
		return FormatUtil.decimalPointOnlyIfNecessary(value);
	}

	private static void assertKeyIsCorrect(String key) {
		if (!key.matches("[\\w%]+")) {
			throw new IllegalArgumentException("Incorrect key: " + key);
		}
	}
}
