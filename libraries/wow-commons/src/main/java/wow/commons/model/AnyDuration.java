package wow.commons.model;

/**
 * User: POlszewski
 * Date: 2025-07-23
 */
public sealed interface AnyDuration extends Comparable<AnyDuration> permits Duration, InfiniteDuration {
	Duration ZERO = new Duration(0);
	InfiniteDuration INFINITE = new InfiniteDuration();

	static AnyDuration parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		if (value.equalsIgnoreCase("INF")) {
			return INFINITE;
		}

		return Duration.parse(value);
	}

	boolean isZero();

	boolean isPositive();

	boolean isInfinite();
}
