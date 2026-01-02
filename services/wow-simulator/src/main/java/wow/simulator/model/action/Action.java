package wow.simulator.model.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.update.Updateable;
import wow.simulator.util.IdGenerator;

import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.IntConsumer;

import static wow.simulator.model.action.ActionStatus.*;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
@RequiredArgsConstructor
public abstract class Action implements Updateable {
	private static final IdGenerator<ActionId> ID_GENERATOR = new IdGenerator<>(ActionId::new);

	@Getter
	protected final ActionId actionId = ID_GENERATOR.newId();

	private record Step(AnyTime time, Runnable action, int order) {}

	private static final Comparator<Step> STEP_COMPARATOR = Comparator
			.comparing(Step::time)
			.thenComparingInt(Step::order);

	private final PriorityQueue<Step> steps = new PriorityQueue<>(STEP_COMPARATOR);

	@Getter
	private ActionStatus status = CREATED;

	private int orderGenerator;

	protected final Clock clock;

	@Override
	public final void update() {
		assertStatus(IN_PROGRESS);

		for (Step step; (step = steps.peek()) != null && clock.timeInThePresent(step.time()); ) {
			steps.poll();
			step.action().run();
			if (isTerminated()) {
				return;
			}
		}

		if (steps.isEmpty()) {
			finish();
		}
	}

	@Override
	public final Optional<AnyTime> getNextUpdateTime() {
		return Optional.ofNullable(getNullableNextUpdateTime());
	}

	private AnyTime getNullableNextUpdateTime() {
		if (isTerminated() || steps.isEmpty()) {
			return null;
		}
		return steps.peek().time();
	}

	public boolean nextUpdateIsInThePresent() {
		var nextUpdateTime = getNullableNextUpdateTime();

		return nextUpdateTime != null && clock.timeInThePresent(nextUpdateTime);
	}

	@Override
	public void onAddedToQueue() {
		if (status != CREATED) {
			throw new IllegalStateException("Only CREATED actions can be added to queue");
		}
		start();
	}

	@Override
	public void onRemovedFromQueue() {
		interrupt();
	}

	public final void start() {
		setStatus(IN_PROGRESS);
		onStarted();
	}

	protected final void finish() {
		setStatus(FINISHED);
		onFinished();
	}

	public final void interrupt() {
		if (isTerminated()) {
			return;
		}
		setStatus(INTERRUPTED);
		onInterrupted();
	}

	protected void onStarted() {
		// void
	}

	protected void onFinished() {
		// void
	}

	protected void onInterrupted() {
		// void
	}

	protected abstract void setUp();

	protected void on(AnyTime time, Runnable action) {
		this.steps.add(new Step(time, action, orderGenerator++));
	}

	protected void fromNowAfter(AnyDuration duration, Runnable action) {
		on(clock.after(duration), action);
	}

	protected void stayIdleFor(AnyDuration duration) {
		fromNowAfter(duration, () -> {});
	}

	protected Time onEachTick(Time start, int numTicks, Duration tickInterval, IntConsumer action) {
		var time = start;
		for (int tickNo = 1; tickNo <= numTicks; ++tickNo) {
			time = time.add(tickInterval);
			int finalTickNo = tickNo;
			on(time, () -> action.accept(finalTickNo));
		}
		return time;
	}

	protected Time fromNowOnEachTick(int numTicks, Duration tickInterval, IntConsumer action) {
		return onEachTick(clock.now(), numTicks, tickInterval, action);
	}

	private void setStatus(ActionStatus status) {
		switch (status) {
			case IN_PROGRESS -> {
				assertStatus(CREATED);
				this.status = IN_PROGRESS;
				Time startTime = clock.now();
				on(startTime, this::setUp);
			}
			case FINISHED -> {
				assertStatus(IN_PROGRESS);
				this.status = FINISHED;
				steps.clear();
			}
			case INTERRUPTED -> {
				assertStatus(IN_PROGRESS);
				this.status = INTERRUPTED;
				steps.clear();
			}
			default -> throw new IllegalArgumentException("" + status);
		}
	}

	public boolean isInProgress() {
		return status == IN_PROGRESS;
	}

	public boolean isTerminated() {
		return status.isTerminal();
	}

	private void assertStatus(ActionStatus expectedStatus) {
		if (status != expectedStatus) {
			throw new IllegalStateException("" + status);
		}
	}

	@Override
	public String toString() {
		return "%s(actionId=%s, status=%s)".formatted(getClass().getSimpleName(), actionId, status);
	}
}
