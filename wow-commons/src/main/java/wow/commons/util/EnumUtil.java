package wow.commons.util;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-08
 */
public final class EnumUtil {
	public static <T extends Enum<T>> T parse(String stringToParse, T[] enumValues, Function<T, String> keyAccessor) {
		if (stringToParse == null) {
			return null;
		}
		return parseOptional(stringToParse, enumValues, keyAccessor)
				.orElseThrow(() -> new IllegalArgumentException(stringToParse));
	}

	public static <T extends Enum<T>> T parse(String stringToParse, T[] enumValues) {
		return parse(stringToParse, enumValues, Enum::name);
	}

	public static <T extends Enum<T>> T tryParse(String stringToParse, T[] enumValues, Function<T, String> keyAccessor) {
		if (stringToParse == null) {
			return null;
		}
		return parseOptional(stringToParse, enumValues, keyAccessor).orElse(null);
	}

	public static <T extends Enum<T>> T tryParse(String stringToParse, T[] enumValues) {
		return tryParse(stringToParse, enumValues, Enum::name);
	}

	private static <T extends Enum<T>> Optional<T> parseOptional(String stringToParse, T[] enumValues, Function<T, String> keyAccessor) {
		return Stream.of(enumValues)
				.filter(value -> keyAccessor.apply(value).equalsIgnoreCase(stringToParse))
				.findAny();
	}

	public static <T extends Enum<T>, V> Map<T, V> cache(Class<T> enumClass, T[] enumValues, Function<T, V> valueProducer) {
		Map<T, V> result = new EnumMap<>(enumClass);

		for (T enumValue : enumValues) {
			result.put(enumValue, valueProducer.apply(enumValue));
		}

		return result;
	}

	private EnumUtil() {}
}
