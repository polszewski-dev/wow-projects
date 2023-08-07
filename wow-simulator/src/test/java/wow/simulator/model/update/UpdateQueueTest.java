package wow.simulator.model.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.simulator.model.action.ActionStatus.*;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
class UpdateQueueTest extends WowSimulatorSpringTest {
	@Test
	void emptyQueue() {
		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(updateQueue.getNextUpdateTime()).isEmpty();
	}

	@Test
	void singleAction() {
		clock.advanceTo(Time.at(10));

		Action action = newAction(10, () -> {});

		action.start();

		updateQueue.add(action);

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(10));

		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(20));

		clock.advanceTo(Time.at(20));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(updateQueue.getNextUpdateTime()).isEmpty();

		assertThat(action.getStatus()).isEqualTo(FINISHED);
	}

	@Test
	void multipleActionsBeginningAndEndingAtTheSameTime() {
		List<String> results = new ArrayList<>();

		clock.advanceTo(Time.at(10));

		Action action1 = newAction(10, () -> results.add("1 " + clock.now()));
		Action action2 = newAction(10, () -> results.add("2 " + clock.now()));
		Action action3 = newAction(10, () -> results.add("3 " + clock.now()));

		action1.start();
		action2.start();
		action3.start();

		updateQueue.add(action1);
		updateQueue.add(action2);
		updateQueue.add(action3);

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(updateQueue.getNextUpdateTime()).isEmpty();
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

		action1.start();
		action2.start();
		action3.start();

		updateQueue.add(action1);
		updateQueue.add(action2);
		updateQueue.add(action3);

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(10));
		assertThat(results).isEmpty();

		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(20));
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(20));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(30));
		assertThat(results).isEqualTo(List.of(
				"1 1 20.000",
				"2 1 20.000",
				"3 1 20.000"
		));

		clock.advanceTo(Time.at(30));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(updateQueue.getNextUpdateTime()).hasValue(Time.at(40));
		assertThat(results).isEqualTo(List.of(
				"1 1 20.000",
				"2 1 20.000",
				"3 1 20.000",
				"1 2 30.000",
				"2 2 30.000",
				"3 2 30.000"
		));

		clock.advanceTo(Time.at(40));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(updateQueue.getNextUpdateTime()).isEmpty();
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
	void canNotAddCreatedAction() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		assertThatThrownBy(() -> updateQueue.add(action)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void canNotAddTerminatedAction() {
		Action action = newAction(0, () -> {});

		action.start();
		action.interrupt();

		assertThat(action.getStatus()).isEqualTo(INTERRUPTED);

		assertThatThrownBy(() -> updateQueue.add(action)).isInstanceOf(IllegalArgumentException.class);
	}

	UpdateQueue updateQueue;

	@BeforeEach
	void setup() {
		setupTestObjects();
		updateQueue = new UpdateQueue();
		updateQueue.setClock(clock);
	}
}