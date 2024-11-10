package wow.commons.client.converter;

import java.util.Collection;
import java.util.List;

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
}
