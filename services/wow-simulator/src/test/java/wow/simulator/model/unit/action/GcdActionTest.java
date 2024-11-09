package wow.simulator.model.unit.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.ActionStatus;
import wow.simulator.model.time.Time;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-08-19
 */
class GcdActionTest extends WowSimulatorSpringTest {
	@Test
	void test() {
		GcdAction action = new GcdAction(Duration.seconds(1.5), testAction());

		clock.advanceTo(Time.at(10));

		action.start();

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.IN_PROGRESS);
		assertThat(action.getNextUpdateTime()).hasValue(Time.at(11.5));

		clock.advanceTo(Time.at(11.5));

		action.update();

		assertThat(action.getStatus()).isEqualTo(ActionStatus.FINISHED);
	}

	private UnitAction testAction() {
		return new UnitAction(player) {
			@Override
			protected void setUp() {

			}
		};
	}

	@BeforeEach
	void setUp() {
		setupTestObjects();
	}
}