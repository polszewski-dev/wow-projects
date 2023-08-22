package wow.simulator.simulation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spells.ResourceType.HEALTH;
import static wow.commons.model.spells.ResourceType.MANA;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
class SimulationTest extends WowSimulatorSpringTest {
	@Test
	void noObjects() {
		simulation = new Simulation(simulationContext);
		simulation.updateUntil(Time.at(30));

		assertEvents();
	}

	@Test
	void noActions() {
		simulation.updateUntil(Time.at(30));

		assertEvents();
	}

	@Test
	void delayedAction() {
		List<String> result = new ArrayList<>();

		simulation.delayedAction(Duration.seconds(5), () -> result.add(getClock().now() + ""));

		simulation.updateUntil(Time.at(4));

		assertThat(result).isEmpty();

		simulation.updateUntil(Time.at(5));

		assertThat(result).isEqualTo(List.of("5.000"));
	}

	@Test
	void getRemainingTime() {
		List<Duration> remainingTimes = new ArrayList<>();

		simulation.add(new Action(clock) {
			@Override
			protected void setUp() {
				on(Time.at(0), () -> remainingTimes.add(simulation.getRemainingTime()));
				on(Time.at(15), () -> remainingTimes.add(simulation.getRemainingTime()));
				on(Time.at(30), () -> remainingTimes.add(simulation.getRemainingTime()));
			}
		});

		simulation.updateUntil(Time.at(30));

		assertThat(remainingTimes).isEqualTo(List.of(
			Duration.seconds(30),
			Duration.seconds(15),
			Duration.seconds(0)
		));
	}

	@Test
	void singleSBCast() {
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void twoSBCasts() {
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
						.beginCast(player, SHADOW_BOLT, target)
						.beginGcd(player),
				at(4.5)
						.endGcd(player),
				at(6)
						.endCast(player, SHADOW_BOLT, target)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@BeforeEach
	void setUp() {
		setupTestObjects();

		handler = new EventCollectingHandler();

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);
	}
}