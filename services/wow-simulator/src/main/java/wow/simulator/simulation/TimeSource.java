package wow.simulator.simulation;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public interface TimeSource {
	Clock getClock();

	default Time now() {
		return getClock().now();
	}

	default Duration getRemainingDurationUntil(Time end) {
		if (end.after(now())) {
			return end.subtract(now());
		}
		return Duration.ZERO;
	}

	default AnyDuration getRemainingDurationUntil(AnyTime end) {
		if (end instanceof Time time) {
			return getRemainingDurationUntil(time);
		}
		return Duration.INFINITE;
	}
}
