package wow.scraper.util;

import java.util.Collection;
import java.util.Objects;

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

	public static <T> void assertSize(String name, Collection<T> x, int expectedSize) {
		if (x.size() != expectedSize) {
			throw new IllegalArgumentException("%s has different size: expected = %s, actual = %s".formatted(name, expectedSize, x.size()));
		}
	}

	private CommonAssertions() {}
}
