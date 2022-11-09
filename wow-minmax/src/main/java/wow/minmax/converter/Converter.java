package wow.minmax.converter;

import java.util.List;
import java.util.Map;
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

	public final List<T> convertList(List<F> list) {
		if (list == null) {
			return null;
		}
		return list.stream()
					.map(this::convert)
					.collect(Collectors.toList());
	}

	public final <K> Map<K, T> convertMap(Map<K, F> map) {
		if (map == null) {
			return null;
		}
		return map.entrySet()
				  .stream()
				  .collect(Collectors.toMap(Map.Entry::getKey, e -> convert(e.getValue())));
	}

	public final List<F> convertBackList(List<T> list) {
		if (list == null) {
			return null;
		}
		return list.stream()
				   .map(this::convertBack)
				   .collect(Collectors.toList());
	}

	public final <K> Map<K, F> convertBackMap(Map<K, T> map) {
		if (map == null) {
			return null;
		}
		return map.entrySet()
				  .stream()
				  .collect(Collectors.toMap(Map.Entry::getKey, e -> convertBack(e.getValue())));
	}
}
