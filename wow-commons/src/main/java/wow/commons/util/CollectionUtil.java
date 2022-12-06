package wow.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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


	private CollectionUtil() {}
}
