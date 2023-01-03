package wow.minmax.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public interface ParametrizedBackConverter<F, T> {
	default F convertBack(T value, Map<String, Object> params) {
		if (value == null) {
			return null;
		}
		return doConvertBack(value, params);
	}

	default List<F> convertBackList(Collection<T> list, Map<String, Object> params) {
		return list.stream()
				.map(x -> convertBack(x, params))
				.collect(Collectors.toList());
	}

	F doConvertBack(T value, Map<String, Object> params);
}
