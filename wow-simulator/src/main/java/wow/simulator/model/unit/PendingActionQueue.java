package wow.simulator.model.unit;

import lombok.AllArgsConstructor;
import wow.simulator.model.action.Action;

import java.util.ArrayList;
import java.util.List;

import static wow.simulator.model.action.ActionStatus.CREATED;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@AllArgsConstructor
public class PendingActionQueue {
	private final List<Action> pendingActions = new ArrayList<>();

	public boolean isEmpty() {
		return pendingActions.isEmpty();
	}

	public void add(Action action) {
		if (action.getStatus() != CREATED) {
			throw new IllegalStateException();
		}
		pendingActions.add(action);
	}

	public Action removeEarliestAction() {
		return pendingActions.remove(0);
	}
}

