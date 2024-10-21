package wow.minmax.converter;

import java.util.Collection;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-01-03
 */
public interface BackConverter<F, T> {
	default F convertBack(T source) {
		if (source == null) {
			return null;
		}
		return doConvertBack(source);
	}

	default List<F> convertBackList(Collection<T> list) {
		return list.stream()
				.map(this::convertBack)
				.toList();
	}

	F doConvertBack(T source);
}
