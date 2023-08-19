package wow.simulator.model.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.time.Time;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.simulator.model.action.ActionStatus.*;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
class ActionTest extends WowSimulatorSpringTest {
	@Test
	void updateCausesEmptyActionToTerminate() {
		Action action = new Action(clock) {
			@Override
			protected void setUp() {

			}
		};

		simulationContext.getClock().advanceTo(Time.at(10));

		assertThat(action.getStatus()).isEqualTo(CREATED);

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@Test
	void scheduledActionIsCalledOnTheCorrectTime() {
		List<Time> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				on(Time.at(20), () -> results.add(clock.now()));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
		assertThat(results).isEqualTo(List.of(Time.at(20)));
	}

	@Test
	void scheduledActionsAreCalledOnTheCorrectTime() {
		List<Time> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				on(Time.at(20), () -> results.add(clock.now()));
				on(Time.at(30), () -> results.add(clock.now()));
				on(Time.at(20), () -> results.add(clock.now()));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(results).isEqualTo(List.of(Time.at(20), Time.at(20)));

		clock.advanceTo(Time.at(30));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
		assertThat(results).isEqualTo(List.of(Time.at(20), Time.at(20), Time.at(30)));
	}

	@Test
	void scheduledActionIsCalledInOrder() {
		List<String> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				on(Time.at(20), () -> results.add("1 " + clock.now()));
				on(Time.at(20), () -> results.add("2 " + clock.now()));
				on(Time.at(20), () -> results.add("3 " + clock.now()));
				on(Time.at(20), () -> results.add("4 " + clock.now()));
				on(Time.at(20), () -> results.add("5 " + clock.now()));
				on(Time.at(20), () -> results.add("6 " + clock.now()));
				on(Time.at(20), () -> results.add("7 " + clock.now()));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
		assertThat(results).isEqualTo(List.of(
				"1 20.000",
				"2 20.000",
				"3 20.000",
				"4 20.000",
				"5 20.000",
				"6 20.000",
				"7 20.000"
		));
	}

	@Test
	void fromNowAfter() {
		List<Time> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				fromNowAfter(Duration.seconds(10), () -> results.add(clock.now()));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
		assertThat(results).isEqualTo(List.of(Time.at(20)));
	}

	@Test
	void onEachTick() {
		List<String> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				Time end = onEachTick(Time.at(20), 4, Duration.seconds(10), tickNo -> results.add(tickNo + " " + clock.now()));
				assertThat(end).isEqualTo(Time.at(60));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(30));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(30));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(40));
		assertThat(results).isEqualTo(List.of(
				"1 30.000"
		));

		clock.advanceTo(Time.at(40));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(50));
		assertThat(results).isEqualTo(List.of(
				"1 30.000",
				"2 40.000"
		));

		clock.advanceTo(Time.at(50));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(60));
		assertThat(results).isEqualTo(List.of(
				"1 30.000",
				"2 40.000",
				"3 50.000"
		));

		clock.advanceTo(Time.at(60));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
		assertThat(results).isEqualTo(List.of(
				"1 30.000",
				"2 40.000",
				"3 50.000",
				"4 60.000"
		));
	}

	@Test
	void fromNowOnEachTick() {
		List<String> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				Time end = fromNowOnEachTick(4, Duration.seconds(10), tickNo -> results.add(tickNo + " " + clock.now()));
				assertThat(end).isEqualTo(Time.at(50));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(30));
		assertThat(results).isEqualTo(List.of(
				"1 20.000"
		));

		clock.advanceTo(Time.at(30));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(40));
		assertThat(results).isEqualTo(List.of(
				"1 20.000",
				"2 30.000"
		));

		clock.advanceTo(Time.at(40));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(50));
		assertThat(results).isEqualTo(List.of(
				"1 20.000",
				"2 30.000",
				"3 40.000"
		));

		clock.advanceTo(Time.at(50));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
		assertThat(results).isEqualTo(List.of(
				"1 20.000",
				"2 30.000",
				"3 40.000",
				"4 50.000"
		));
	}

	@Test
	void interruptSameTimes() {
		List<String> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				on(Time.at(20), () -> results.add("1 " + clock.now()));
				on(Time.at(20), () -> results.add("2 " + clock.now()));
				on(Time.at(20), () -> results.add("3 " + clock.now()));
				on(Time.at(20), this::interrupt);
				on(Time.at(20), () -> results.add("5 " + clock.now()));
				on(Time.at(20), () -> results.add("6 " + clock.now()));
				on(Time.at(20), () -> results.add("7 " + clock.now()));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.INTERRUPTED);
		assertThat(results).isEqualTo(List.of(
				"1 20.000",
				"2 20.000",
				"3 20.000"
		));
	}

	@Test
	void interruptDifferentTimes() {
		List<String> results = new ArrayList<>();

		Action action = new Action(clock) {
			@Override
			protected void setUp() {
				on(Time.at(10), () -> results.add("1 " + clock.now()));
				on(Time.at(20), this::interrupt);
				on(Time.at(30), () -> results.add("3 " + clock.now()));
			}
		};

		clock.advanceTo(Time.at(10));

		action.start();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEqualTo(List.of(
				"1 10.000"
		));

		clock.advanceTo(Time.at(20));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.INTERRUPTED);
		assertThat(results).isEqualTo(List.of(
				"1 10.000"
		));
	}

	@Test
	void startAndFinish() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		action.start();

		assertThat(action.getStatus()).isEqualTo(IN_PROGRESS);

		action.update();

		assertThat(action.getStatus()).isEqualTo(FINISHED);
	}

	@Test
	void startAndInterrupt() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		action.start();

		assertThat(action.getStatus()).isEqualTo(IN_PROGRESS);

		action.interrupt();

		assertThat(action.getStatus()).isEqualTo(INTERRUPTED);
	}

	@Test
	void finishWithoutStarting() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		assertThatThrownBy(action::finish).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void interruptWithoutStarting() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		assertThatThrownBy(action::interrupt).isInstanceOf(IllegalStateException.class);
	}

	@BeforeEach
	void setup() {
		setupTestObjects();
	}
}