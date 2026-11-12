package wow.simulator.model.unit;

import lombok.Setter;
import wow.simulator.model.action.Action;

import java.util.ArrayList;
import java.util.List;

import static wow.simulator.model.action.ActionStatus.CREATED;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public class PendingActionQueue<T extends Action> {
	private final List<T> pendingActions = new ArrayList<>();

	public enum Mode {
		APPEND,
		PREPEND
	}

	@Setter
	private Mode mode = Mode.APPEND;
	private T lastPrependedAction;

	public boolean isEmpty() {
		return pendingActions.isEmpty();
	}

	public void add(T action) {
		if (action.getStatus() != CREATED) {
			throw new IllegalStateException();
		}

		switch (mode) {
			case APPEND ->
				pendingActions.add(action);

			case PREPEND -> {
				if (lastPrependedAction == null) {
					pendingActions.addFirst(action);
				} else {
					var position = pendingActions.indexOf(lastPrependedAction);
					pendingActions.add(position + 1, action);
				}

				this.lastPrependedAction = action;
			}
		}
	}

	public T removeEarliestAction() {
		T result = pendingActions.removeFirst();

		if (result == lastPrependedAction) {
			this.lastPrependedAction = null;
		}

		return result;
	}

	public void reset() {
		pendingActions.clear();
		this.lastPrependedAction = null;
	}
}

