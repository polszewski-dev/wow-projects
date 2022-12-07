package wow.minmax.converter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public abstract class Converter<F, T> {
	public final T convert(F value) {
		if (value == null) {
			return null;
		}
		return doConvert(value);
	}

	public final F convertBack(T value) {
		if (value == null) {
			return null;
		}
		return doConvertBack(value);
	}

	protected abstract T doConvert(F value);

	protected F doConvertBack(T value) {
		throw new IllegalArgumentException("Not implemented");
	}

	public final List<T> convertList(Collection<F> list) {
		return list.stream()
					.map(this::convert)
					.collect(Collectors.toList());
	}

	public final List<F> convertBackList(List<T> list) {
		return list.stream()
				   .map(this::convertBack)
				   .collect(Collectors.toList());
	}
}
