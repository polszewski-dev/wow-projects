package wow.simulator.model.update;

import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
import wow.simulator.model.action.Action;
import wow.simulator.model.action.ActionStatus;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;

import java.util.*;

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

	public void add(Time time, Runnable runnable) {
		if (clock.timeInThePast(time)) {
			throw new IllegalArgumentException("Time %s is in the past (now = %s)".formatted(time, clock.now()));
		}
		var delay = time.subtract(clock.now());
		add(delay, runnable);
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

	public void rescheduleInterruptedActionToPresent(Action action, AnyTime updateTime) {
		if (action.getStatus() != ActionStatus.INTERRUPTED) {
			throw new IllegalStateException();
		}

		if (updateTime == null || clock.timeInThePresent(updateTime)) {
			return;
		}

		var actions = actionsByTime.get(updateTime);

		if (actions == null || !actions.contains(action)) {
			return;
		}

		actions.remove(action);
		actionsByTime.get(clock.now()).add(action);
	}

	public void interruptUnfinishedActions() {
		var actions = actionsByTime.values()
				.stream()
				.flatMap(Collection::stream)
				.toList();

		actionsByTime.clear();

		for (var action : actions) {
			if (action.isInProgress()) {
				action.interrupt();
				action.onRemovedFromQueue();
			}
		}
	}
}
