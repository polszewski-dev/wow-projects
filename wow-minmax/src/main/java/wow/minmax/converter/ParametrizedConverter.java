package wow.minmax.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public abstract class ParametrizedConverter<F, T> {
	public final T convert(F value, Map<String, Object> params) {
		if (value == null) {
			return null;
		}
		return doConvert(value, params);
	}

	public final F convertBack(T value, Map<String, Object> params) {
		if (value == null) {
			return null;
		}
		return doConvertBack(value, params);
	}

	protected abstract T doConvert(F value, Map<String, Object> params);

	protected F doConvertBack(T value, Map<String, Object> params) {
		throw new IllegalArgumentException("Not implemented");
	}

	public final List<T> convertList(Collection<F> list, Map<String, Object> params) {
		return list.stream()
					.map(value -> convert(value, params))
					.collect(Collectors.toList());
	}

	public final List<F> convertBackList(List<T> list, Map<String, Object> params) {
		return list.stream()
				   .map(value  -> convertBack(value, params))
				   .collect(Collectors.toList());
	}
}
