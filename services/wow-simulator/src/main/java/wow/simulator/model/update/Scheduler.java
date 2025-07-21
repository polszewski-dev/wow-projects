package wow.simulator.model.update;

import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.time.Clock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2025-01-25
 */
@RequiredArgsConstructor
public class Scheduler {
	private final Clock clock;

	private final TreeMap<AnyTime, List<Action>> actionsByTime = new TreeMap<>();

	public void add(Action action) {
		action.onAddedToQueue();
		schedule(action);
	}

	public void add(Duration delay, Runnable runnable) {
		var action = new Action(clock) {
			@Override
			protected void setUp() {
				fromNowAfter(delay, runnable);
			}
		};

		add(action);
	}

	private void schedule(Action action) {
		var nextUpdateTime = action.getNextUpdateTime().orElseThrow();

		if (clock.timeInThePast(nextUpdateTime)) {
			throw new IllegalArgumentException();
		}

		actionsByTime
				.computeIfAbsent(nextUpdateTime, x -> new ArrayList<>())
				.add(action);
	}

	public void updateAllPresentActions() {
		var actions = actionsByTime.get(clock.now());

		if (actions == null) {
			return;
		}

		for (int i = 0; i < actions.size(); ++i) {
			var action = actions.get(i);

			if (action.isInProgress()) {
				action.update();
			}

			if (action.isInProgress() && action.nextUpdateIsInThePresent()) {
				actions.add(action);
			}
		}

		for (var action : actions) {
			if (action.isTerminated()) {
				action.onRemovedFromQueue();
			} else {
				schedule(action);
			}
		}

		actionsByTime.remove(clock.now());
	}

	public Optional<AnyTime> getNextUpdateTime() {
		if (actionsByTime.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(actionsByTime.firstKey());
	}

	public boolean isEmpty() {
		return actionsByTime.isEmpty();
	}
}
