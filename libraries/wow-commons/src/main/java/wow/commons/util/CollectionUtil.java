package wow.commons.util;

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
					return list.get(0);
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
			case 1 -> Optional.of(list.get(0));
			default -> throw new IllegalArgumentException("" + list);
		};
	}

	private CollectionUtil() {}
}
