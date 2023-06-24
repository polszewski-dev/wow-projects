package wow.scraper.util;

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

	private CommonAssertions() {}
}
