package wow.simulator.simulation;

import wow.simulator.model.time.Clock;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public interface TimeAware {
	void setClock(Clock clock);
}
