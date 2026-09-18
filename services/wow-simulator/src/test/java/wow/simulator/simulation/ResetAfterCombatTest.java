package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.time.Time;
import wow.simulator.util.TestEvent;
import wow.simulator.util.TestEventCollectingHandler;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;
import static wow.test.commons.AssetNames.IMP;

/**
 * User: POlszewski
 * Date: 2026-09-16
 */
class ResetAfterCombatTest extends WowSimulatorSpringTest {
	@Test
	void two_consecutive_simulations_result_in_exactly_the_same_event_lists() {
		setupObjects();

		var events1 = executeScenario();

		player.resetAfterCombat();
		target.resetAfterCombat();

		var events2 = executeScenario();

		assertThat(events1).isEqualTo(events2);
	}

	@Test
	void resetting_killed_target_brings_it_to_live() {
		setupObjects();

		scenario.runBeforeCombat(
				() -> player.getSimulation().runAt(
						Time.at(70),
						() -> instaKill(player, target)
				)
		);

		executeScenario();

		assertThat(target.isDead()).isTrue();

		target.resetAfterCombat();

		assertThat(target.isDead()).isFalse();
		assertThat(target.getCurrentHealth()).isPositive();
	}

	@Test
	void resetting_player_unsummons_pet() {
		setupObjects();

		player.getAssets().enable(IMP);

		executeScenario();

		assertThat(player.getActivePet()).isNotNull();

		player.resetAfterCombat();

		assertThat(player.getActivePet()).isNull();
	}

	private List<TestEvent> executeScenario() {
		var handler = new TestEventCollectingHandler();

		handler.setIgnoreRegen(false);

		scenario.execute(player.getRaid(), target, List.of(handler));

		return handler.getEvents();
	}

	private void setupObjects() {
		simulationContext = getSimulationContext();
		player = getNakedPlayer(WARLOCK, ORC, "Player");
		target = getEnemy("Target");

		player.setScript("warlock-destro-shadow");

		scenario = getScenario(60);
	}

	private TestScenario scenario;
}
