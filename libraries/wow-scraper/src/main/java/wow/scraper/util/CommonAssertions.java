package wow.scraper.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public final class CommonAssertions {
	public static <T> void assertBothAreEqual(String name, T x, T y) {
		if (!Objects.equals(x, y)) {
			throw new IllegalArgumentException("%s has different values: x = %s, y = %s".formatted(name, x, y));
		}
	}

	public static <T> void assertSizeNoLargerThan(String name, Collection<T> x, int expectedSize) {
		if (x.size() > expectedSize) {
			throw new IllegalArgumentException("%s has larger size: expected = %s, actual = %s".formatted(name, expectedSize, x.size()));
		}
	}

	public static <T> void assertNoDuplicates(Collection<T> elements, Function<T, String> toUniqueStringMapper) {
		List<String> duplicatePatterns = elements.stream()
				.map(toUniqueStringMapper)
				.collect(Collectors.groupingBy(x -> x, Collectors.counting()))
				.entrySet().stream()
				.filter(x -> x.getValue() > 1)
				.map(Map.Entry::getKey)
				.toList();

		if (!duplicatePatterns.isEmpty()) {
			throw new IllegalArgumentException("Duplicates detected: " + duplicatePatterns);
		}
	}

	private CommonAssertions() {}
}
