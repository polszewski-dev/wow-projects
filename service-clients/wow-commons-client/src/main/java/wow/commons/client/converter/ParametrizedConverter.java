package wow.commons.client.converter;

import java.util.Collection;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface ParametrizedConverter<F, T, P> {
	default T convert(F source, P param) {
		if (source == null) {
			return null;
		}
		return doConvert(source, param);
	}

	default List<T> convertList(Collection<F> list, P param) {
		return list.stream()
				.map(x -> convert(x, param))
				.toList();
	}

	T doConvert(F source, P param);
}
