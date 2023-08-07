package wow.simulator.model.action;

import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
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

	protected final ActionId actionId = ID_GENERATOR.newId();

	private record Step(Time time, Runnable action, int order) {}

	private static final Comparator<Step> STEP_COMPARATOR = Comparator
			.comparing(Step::time)
			.thenComparingInt(Step::order);

	private final PriorityQueue<Step> steps = new PriorityQueue<>(STEP_COMPARATOR);
	private ActionStatus status = CREATED;

	private int orderGenerator;

	protected final Clock clock;

	@Override
	public void update() {
		assertStatus(IN_PROGRESS);

		for (Step step; (step = steps.peek()) != null && clock.timeInThePresent(step.time()); ) {
			steps.poll();
			step.action().run();
			if (status.isTerminal()) {
				return;
			}
		}

		if (steps.isEmpty()) {
			finish();
		}
	}

	@Override
	public Optional<Time> getNextUpdateTime() {
		if (status.isTerminal()) {
			return Optional.empty();
		}
		if (!steps.isEmpty()) {
			return Optional.of(steps.peek().time());
		}
		return Optional.empty();
	}

	public void start() {
		setStatus(IN_PROGRESS);
	}

	public void finish() {
		setStatus(FINISHED);
	}

	public void interrupt() {
		setStatus(INTERRUPTED);
	}

	protected abstract void setUp();

	protected void on(Time time, Runnable action) {
		this.steps.add(new Step(time, action, orderGenerator++));
	}

	protected void fromNowAfter(Duration duration, Runnable action) {
		on(clock.after(duration), action);
	}

	protected Time onEachTick(Time start, int numTicks, Duration tickInterval, IntConsumer action) {
		Time time = start;
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

	public ActionStatus getStatus() {
		return status;
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
		return "actionId=" + actionId + ", status=" + status;
	}
}
