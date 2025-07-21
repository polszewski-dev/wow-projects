package wow.simulator.model.time;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2025-07-23
 */
public record TimeInInfinity() implements AnyTime {
	@Override
	public boolean isInInfinity() {
		return true;
	}

	@Override
	public TimeInInfinity add(Duration duration) {
		return this;
	}

	@Override
	public TimeInInfinity add(AnyDuration anyDuration) {
		return this;
	}

	@Override
	public int compareTo(AnyTime anyTime) {
		if (anyTime.isInInfinity()) {
			return 0;
		}
		return 1;
	}

	@Override
	public String toString() {
		return "INF";
	}
}
