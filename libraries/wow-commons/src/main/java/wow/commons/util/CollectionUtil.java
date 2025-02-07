package wow.commons.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-06
 */
public final class CollectionUtil {
	public static <T> Collector<T, ?, T> toSingleton() {
		return Collectors.collectingAndThen(
				Collectors.toList(),
				list -> {
					if (list.size() != 1) {
						throw new IllegalArgumentException("" + list);
					}
					return list.getFirst();
				}
		);
	}

	public static <T> Collector<T, ?, Optional<T>> toOptionalSingleton() {
		return Collectors.collectingAndThen(
				Collectors.toList(),
				CollectionUtil::getUniqueResult
		);
	}

	public static <T> Optional<T> getUniqueResult(List<T> list) {
		return switch (list.size()) {
			case 0 -> Optional.empty();
			case 1 -> Optional.of(list.getFirst());
			default -> throw new IllegalArgumentException("" + list);
		};
	}

	public static <T> List<T> join(List<T> list1, List<T> list2) {
		if (list1 == null || list1.isEmpty()) {
			return list2;
		}
		if (list2 == null || list2.isEmpty()) {
			return list1;
		}
		var both = new ArrayList<>(list1);
		both.addAll(list2);
		return both;
	}

	private CollectionUtil() {}
}
