package wow.minmax.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface ParametrizedConverter<F, T> {
	default T convert(F source, Map<String, Object> params) {
		if (source == null) {
			return null;
		}
		return doConvert(source, params);
	}

	default List<T> convertList(Collection<F> list, Map<String, Object> params) {
		return list.stream()
				.map(x -> convert(x, params))
				.toList();
	}

	T doConvert(F source, Map<String, Object> params);
}
