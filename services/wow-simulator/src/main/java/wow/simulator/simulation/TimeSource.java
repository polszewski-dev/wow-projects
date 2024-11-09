package wow.simulator.simulation;

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
}
