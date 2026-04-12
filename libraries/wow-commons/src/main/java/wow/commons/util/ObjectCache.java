package wow.commons.util;

import java.util.HashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2026-04-11
 */
public final class ObjectCache {
	private static Map<Object, Object> map = new HashMap<>();

	public static <T> T cache(T value) {
		if (map == null) {
			return value;
		}
		if (map.containsKey(value)) {
			return (T) map.get(value);
		}
		map.put(value, value);
		return value;
	}

	public static void discard() {
		map = null;
	}

	private ObjectCache() {}
}
