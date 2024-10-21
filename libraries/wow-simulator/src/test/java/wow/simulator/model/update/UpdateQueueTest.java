package wow.simulator.model.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
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
	void createdActionStartedAutomatically() {
		Action action = newAction(0, () -> {});

		assertThat(action.getStatus()).isEqualTo(CREATED);

		assertThatNoException().isThrownBy(() -> updateQueue.add(action));
	}

	@Test
	void canNotAddTerminatedAction() {
		Action action = newAction(0, () -> {});

		action.start();
		action.interrupt();

		assertThat(action.getStatus()).isEqualTo(INTERRUPTED);

		assertThatThrownBy(() -> updateQueue.add(action)).isInstanceOf(IllegalStateException.class);
	}

	@Test
	void remove() {
		List<String> results = new ArrayList<>();

		Action action = removeAction("1", results);

		var handle = updateQueue.add(action);
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(10));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000"
		));

		assertThat(action.getStatus()).isEqualTo(IN_PROGRESS);

		updateQueue.remove(handle);

		assertThat(action.getStatus()).isEqualTo(INTERRUPTED);

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"1 removed"
		));

		clock.advanceTo(Time.at(20));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"1 removed"
		));
	}

	@Test
	void removeIf() {
		List<String> results = new ArrayList<>();

		Action action1 = removeAction("1", results);
		Action action2 = removeAction("2", results);
		Action action3 = removeAction("3", results);
		Action action4 = removeAction("4", results);
		Action action5 = removeAction("5", results);

		updateQueue.add(action1);
		updateQueue.add(action2);
		updateQueue.add(action3);
		updateQueue.add(action4);
		updateQueue.add(action5);
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(10));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000",
				"3 1 10.000",
				"4 1 10.000",
				"5 1 10.000"
		));

		assertThat(action1.getStatus()).isEqualTo(IN_PROGRESS);
		assertThat(action2.getStatus()).isEqualTo(IN_PROGRESS);

		updateQueue.removeIf(x -> List.of("1", "3", "5").contains(((RemoveAction) x).name));

		assertThat(action1.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action2.getStatus()).isEqualTo(IN_PROGRESS);
		assertThat(action3.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action4.getStatus()).isEqualTo(IN_PROGRESS);
		assertThat(action5.getStatus()).isEqualTo(INTERRUPTED);

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000",
				"3 1 10.000",
				"4 1 10.000",
				"5 1 10.000",
				"1 removed",
				"3 removed",
				"5 removed"
		));

		clock.advanceTo(Time.at(20));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000",
				"3 1 10.000",
				"4 1 10.000",
				"5 1 10.000",
				"1 removed",
				"3 removed",
				"5 removed",
				"2 2 20.000",
				"4 2 20.000"
		));

		updateQueue.removeIf(x -> true);

		assertThat(action1.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action2.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action3.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action4.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action5.getStatus()).isEqualTo(INTERRUPTED);

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000",
				"3 1 10.000",
				"4 1 10.000",
				"5 1 10.000",
				"1 removed",
				"3 removed",
				"5 removed",
				"2 2 20.000",
				"4 2 20.000",
				"2 removed",
				"4 removed"
		));
	}

	@Test
	void clear() {
		List<String> results = new ArrayList<>();

		Action action1 = removeAction("1", results);
		Action action2 = removeAction("2", results);

		updateQueue.add(action1);
		updateQueue.add(action2);
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEmpty();

		clock.advanceTo(Time.at(10));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isFalse();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000"
		));

		assertThat(action1.getStatus()).isEqualTo(IN_PROGRESS);
		assertThat(action2.getStatus()).isEqualTo(IN_PROGRESS);

		updateQueue.clear();

		assertThat(action1.getStatus()).isEqualTo(INTERRUPTED);
		assertThat(action2.getStatus()).isEqualTo(INTERRUPTED);

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000",
				"1 removed",
				"2 removed"
		));

		clock.advanceTo(Time.at(20));
		updateQueue.updateAllPresentActions();

		assertThat(updateQueue.isEmpty()).isTrue();
		assertThat(results).isEqualTo(List.of(
				"1 1 10.000",
				"2 1 10.000",
				"1 removed",
				"2 removed"
		));
	}

	private RemoveAction removeAction(String actionName, List<String> results) {
		return new RemoveAction(actionName, results);
	}

	private class RemoveAction extends Action {
		private final String name;
		private final List<String> results;

		public RemoveAction(String name, List<String> results) {
			super(UpdateQueueTest.this.clock);
			this.name = name;
			this.results = results;
		}

		@Override
		protected void setUp() {
			fromNowOnEachTick(3, Duration.seconds(10), tickNo -> results.add(name + " " + tickNo + " " + clock.now()));
		}

		@Override
		public void onRemovedFromQueue() {
			super.onRemovedFromQueue();
			results.add(name + " removed");
		}
	}

	UpdateQueue<Action> updateQueue;

	@BeforeEach
	void setup() {
		setupTestObjects();
		updateQueue = new UpdateQueue<>();
		updateQueue.setClock(clock);
	}
}