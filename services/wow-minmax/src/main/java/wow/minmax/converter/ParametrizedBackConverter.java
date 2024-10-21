package wow.minmax.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public interface ParametrizedBackConverter<F, T> {
	default F convertBack(T source, Map<String, Object> params) {
		if (source == null) {
			return null;
		}
		return doConvertBack(source, params);
	}

	default List<F> convertBackList(Collection<T> list, Map<String, Object> params) {
		return list.stream()
				.map(x -> convertBack(x, params))
				.toList();
	}

	F doConvertBack(T source, Map<String, Object> params);
}
