package wow.simulator.model.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.Action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.simulator.model.action.ActionStatus.CREATED;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class PendingActionQueueTest extends WowSimulatorSpringTest {
	@Test
	void test() {
		assertThat(queue.isEmpty()).isTrue();

		Action action1 = newAction();
		Action action2 = newAction();
		Action action3 = newAction();

		queue.add(action1);
		queue.add(action2);
		queue.add(action3);

		assertThat(action1.getStatus()).isEqualTo(CREATED);
		assertThat(action2.getStatus()).isEqualTo(CREATED);
		assertThat(action3.getStatus()).isEqualTo(CREATED);

		assertThat(queue.isEmpty()).isFalse();

		assertThat(queue.removeEarliestAction()).isEqualTo(action1);
		assertThat(action1.getStatus()).isEqualTo(CREATED);

		assertThat(queue.removeEarliestAction()).isEqualTo(action2);
		assertThat(action2.getStatus()).isEqualTo(CREATED);

		assertThat(queue.removeEarliestAction()).isEqualTo(action3);
		assertThat(action3.getStatus()).isEqualTo(CREATED);

		assertThat(queue.isEmpty()).isTrue();
	}

	@Test
	void onlyCreated() {
		Action action = newAction();

		action.start();

		assertThatThrownBy(() -> queue.add(action)).isInstanceOf(IllegalStateException.class);

		action.update();

		assertThatThrownBy(() -> queue.add(action)).isInstanceOf(IllegalStateException.class);
	}

	Action newAction() {
		return newAction(0, () -> {});
	}

	PendingActionQueue<Action> queue;

	@BeforeEach
	void setup() {
		setupTestObjects();
		queue = new PendingActionQueue<>();
	}
}