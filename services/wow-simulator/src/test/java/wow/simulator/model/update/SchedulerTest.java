package wow.simulator.model.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static wow.simulator.model.action.ActionStatus.*;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
class SchedulerTest extends WowSimulatorSpringTest {
	@Test
	void emptyQueue() {
		assertThat(scheduler.isEmpty()).isTrue();
		assertThat(scheduler.getNextUpdateTime()).isEmpty();
	}

	@Test
	void singleAction() {
		clock.advanceTo(Time.at(10));

		Action action = newAction(10, () -> {});

		scheduler.add(action);

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(10));

		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(20));

		clock.advanceTo(Time.at(20));
		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isTrue();
		assertThat(scheduler.getNextUpdateTime()).isEmpty();

		assertThat(action.getStatus()).isEqualTo(FINISHED);
	}

	@Test
	void multipleActionsBeginningAndEndingAtTheSameTime() {
		List<String> results = new ArrayList<>();

		clock.advanceTo(Time.at(10));

		Action action1 = newAction(10, () -> results.add("1 " + clock.now()));
		Action action2 = newAction(10, () -> results.add("2 " + clock.now()));
		Action action3 = newAction(10, () -> results.add("3 " + clock.now()));

		scheduler.add(action1);
		scheduler.add(action2);
		scheduler.add(action3);

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));
		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isTrue();
		assertThat(scheduler.getNextUpdateTime()).isEmpty();
		assertThat(results).isEqualTo(List.of(
				"1 20.000",
				"2 20.000",
				"3 20.000"
		));

		assertThat(action1.getStatus()).isEqualTo(FINISHED);
		assertThat(action2.getStatus()).isEqualTo(FINISHED);
		assertThat(action3.getStatus()).isEqualTo(FINISHED);
	}

	@Test
	void multipleMultiStepActionsBeginningAndEndingAtTheSameTime() {
		List<String> results = new ArrayList<>();

		clock.advanceTo(Time.at(10));

		Action action1 = newTickAction(3, 10, tickNo -> results.add("1 " + tickNo + " " + clock.now()));
		Action action2 = newTickAction(3, 10, tickNo -> results.add("2 " + tickNo + " " + clock.now()));
		Action action3 = newTickAction(3, 10, tickNo -> results.add("3 " + tickNo + " " + clock.now()));

		scheduler.add(action1);
		scheduler.add(action2);
		scheduler.add(action3);

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));
		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(30));
		assertThat(results).isEqualTo(List.of(
				"1 1 20.000",
				"2 1 20.000",
				"3 1 20.000"
		));

		clock.advanceTo(Time.at(30));
		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isFalse();
		assertThat(scheduler.getNextUpdateTime()).hasValue(Time.at(40));
		assertThat(results).isEqualTo(List.of(
				"1 1 20.000",
				"2 1 20.000",
				"3 1 20.000",
				"1 2 30.000",
				"2 2 30.000",
				"3 2 30.000"
		));

		clock.advanceTo(Time.at(40));
		scheduler.updateAllPresentActions();

		assertThat(scheduler.isEmpty()).isTrue();
		assertThat(scheduler.getNextUpdateTime()).isEmpty();
		assertThat(results).isEqualTo(List.of(
				"1 1 20.000",
				"2 1 20.000",
				"3 1 20.000",
				"1 2 30.000",
				"2 2 30.000",
				"3 2 30.000",
				"1 3 40.000",
				"2 3 40.000",
				"3 3 40.000"
		));

		assertThat(action1.getStatus()).isEqualTo(FINISHED);
		assertThat(action2.getStatus()).isEqualTo(FINISHED);
		assertThat(action3.getStatus()).isEqualTo(FINISHED);
	}

	@Test
	void createdActionStartedAutomatically() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		assertThatNoException().isThrownBy(() -> scheduler.add(action));
	}

	@Test
	void canNotAddTerminatedAction() {
		Action action = newAction(0, () -> {});

		action.start();
		action.interrupt();

		assertThat(action.getStatus()).isEqualTo(INTERRUPTED);

		assertThatThrownBy(() -> scheduler.add(action)).isInstanceOf(IllegalStateException.class);
	}

	Scheduler scheduler;

	@BeforeEach
	void setup() {
		setupTestObjects();
		scheduler = new Scheduler(clock);
	}
}