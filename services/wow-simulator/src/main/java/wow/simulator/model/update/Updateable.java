package wow.simulator.model.update;

import wow.simulator.model.time.AnyTime;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Updateable {
	void update();

	Optional<AnyTime> getNextUpdateTime();

	default void onAddedToQueue() {}

	default void onRemovedFromQueue() {}
}
