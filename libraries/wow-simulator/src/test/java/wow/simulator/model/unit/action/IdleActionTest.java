package wow.simulator.model.unit.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.ActionStatus;
import wow.simulator.model.time.Time;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-11
 */
class IdleActionTest extends WowSimulatorSpringTest {
	@Test
	void test() {
		IdleAction action = new IdleAction(player, Time.at(50));

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(50));

		clock.advanceTo(Time.at(50));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	@BeforeEach
	void setup() {
		setupTestObjects();
	}
}