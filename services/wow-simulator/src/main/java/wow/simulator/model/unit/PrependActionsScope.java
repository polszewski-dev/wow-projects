package wow.simulator.model.unit;

import wow.simulator.model.unit.impl.UnitImpl;

import static wow.simulator.model.unit.PendingActionQueue.Mode.APPEND;
import static wow.simulator.model.unit.PendingActionQueue.Mode.PREPEND;

/**
 * User: POlszewski
 * Date: 2026-11-12
 */
public class PrependActionsScope implements AutoCloseable {
	private final UnitImpl unit;

	public PrependActionsScope(UnitImpl unit) {
		this.unit = unit;
		unit.setPendingActionQueueMode(PREPEND);
	}

	@Override
	public void close() {
		unit.setPendingActionQueueMode(APPEND);
	}
}
