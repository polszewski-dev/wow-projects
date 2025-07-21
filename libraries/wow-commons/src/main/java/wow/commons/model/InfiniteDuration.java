package wow.commons.model;

/**
 * User: POlszewski
 * Date: 2025-07-23
 */
public record InfiniteDuration() implements AnyDuration {
	@Override
	public boolean isZero() {
		return false;
	}

	@Override
	public boolean isPositive() {
		return true;
	}

	@Override
	public boolean isInfinite() {
		return true;
	}

	@Override
	public int compareTo(AnyDuration anyDuration) {
		if (anyDuration.isInfinite()) {
			return 0;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "INF";
	}
}
