package wow.simulator.model.update;

import wow.simulator.model.time.Time;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
public interface AbstractUpdateable {
	Optional<Time> getNextUpdateTime();

	default void onAddedToQueue() {}

	default void onRemovedFromQueue() {}
}
