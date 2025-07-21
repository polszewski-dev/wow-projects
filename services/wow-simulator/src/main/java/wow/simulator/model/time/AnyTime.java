package wow.simulator.model.time;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2025-07-23
 */
public sealed interface AnyTime extends Comparable<AnyTime> permits Time, TimeInInfinity {
	TimeInInfinity TIME_IN_INFINITY = new TimeInInfinity();

	boolean isInInfinity();

	AnyTime add(Duration duration);

	AnyTime add(AnyDuration anyDuration);

	default boolean after(Time time) {
		return this.compareTo(time) > 0;
	}

	default boolean before(Time time) {
		return this.compareTo(time) < 0;
	}
}
