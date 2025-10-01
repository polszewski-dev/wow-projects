package wow.commons.client.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface Converter<F, T> {
	default T convert(F source) {
		if (source == null) {
			return null;
		}
		return doConvert(source);
	}

	default List<T> convertList(Collection<F> list) {
		return list.stream()
				.map(this::convert)
				.toList();
	}

	T doConvert(F source);

	default <K> Map<K, T> convertMap(Map<K, F> map) {
		return convertMap(map, Function.identity());
	}

	default <K1, K2> Map<K2, T> convertMap(Map<K1, F> map, Function<K1, K2> keyMapper) {
		return map.entrySet().stream()
				.collect(toMap(
						e -> keyMapper.apply(e.getKey()),
						e -> convert(e.getValue())
				));
	}
}
