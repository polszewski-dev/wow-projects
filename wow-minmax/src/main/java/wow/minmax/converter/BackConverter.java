package wow.minmax.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public interface BackConverter<F, T> {
	default F convertBack(T value) {
		if (value == null) {
			return null;
		}
		return doConvertBack(value);
	}

	default List<F> convertBackList(Collection<T> list) {
		return list.stream()
				.map(this::convertBack)
				.collect(Collectors.toList());
	}

	F doConvertBack(T value);
}
