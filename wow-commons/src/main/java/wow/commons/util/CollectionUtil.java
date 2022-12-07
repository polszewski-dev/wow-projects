package wow.commons.util;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-12-06
 */
public final class CollectionUtil {
	public static <T> boolean hasCommonElements(Collection<T> first, Collection<T> second) {
		return !getCommonElements(first, second).isEmpty();
	}

	public static <T> List<T> getCommonElements(Collection<T> first, Collection<T> second) {
		List<T> result = new ArrayList<>(first);
		result.retainAll(second);
		return result;
	}

	public static <T> boolean hasCommonCriteria(List<T> first, List<T> second) {
		return getCommonCriteria(first, second).isPresent();
	}

	public static <T> Optional<List<T>> getCommonCriteria(List<T> first, List<T> second) {
		if (first.isEmpty()) {
			return Optional.of(second);
		}
		if (second.isEmpty()) {
			return Optional.of(first);
		}
		List<T> commonElements = getCommonElements(first, second);
		if (commonElements.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(commonElements);
	}

	public static  <T> T mergeValues(T first, T second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (Objects.equals(first, second)) {
			return first;
		}
		throw new IllegalArgumentException(String.format("Both elements are not null: first=%s, second=%s", first, second));
	}

	public static <T> List<T> mergeCriteria(List<T> first, List<T> second) {
		return getCommonCriteria(first, second)
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Both lists have no common elements: first=%s, second=%s", first, second)));
	}

	private CollectionUtil() {}
}
