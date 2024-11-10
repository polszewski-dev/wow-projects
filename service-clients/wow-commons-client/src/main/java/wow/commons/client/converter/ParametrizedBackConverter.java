package wow.commons.client.converter;

import java.util.Collection;
import java.util.List;

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

	F doConvertBack(T source, P param);
}
