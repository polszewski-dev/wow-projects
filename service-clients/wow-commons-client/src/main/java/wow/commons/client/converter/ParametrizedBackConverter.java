package wow.commons.client.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public interface ParametrizedBackConverter<F, T, P> {
	default F convertBack(T source, P param) {
		if (source == null) {
			return null;
		}
		return doConvertBack(source, param);
	}

	default List<F> convertBackList(Collection<T> list, P param) {
		return list.stream()
				.map(x -> convertBack(x, param))
				.toList();
	}

	default <K> Map<K, F> convertBackMap(Map<K, T> map, P param) {
		return convertBackMap(map, Function.identity(), param);
	}

	default <K1, K2> Map<K1, F> convertBackMap(Map<K2, T> map, Function<K2, K1> keyMapper, P param) {
		return map.entrySet().stream()
				.collect(toMap(
						e -> keyMapper.apply(e.getKey()),
						e -> convertBack(e.getValue(), param)
				));
	}


	F doConvertBack(T source, P param);
}
