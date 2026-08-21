package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.Duration;
import wow.simulator.log.handler.ConsoleGameLogHandler;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.spell.SpellSimulationTest;

import java.util.List;

import static wow.test.commons.AbilityNames.CHAIN_OF_THE_TWILIGHT_OWL;
import static wow.test.commons.AbilityNames.EYE_OF_THE_NIGHT;

/**
 * User: POlszewski
 * Date: 2026-08-21
 */
public class NecklaceTest extends SpellSimulationTest {

	@Autowired
	SimulatorService simulationService;

	@Test
	void necklace_abilities_are_activated_during_buff_phase() {
		equip(player2, "Chain of the Twilight Owl");
		equip(player3, "Eye of the Night");

		simulationService.simulate(player.getRaid(), target, Duration.seconds(120), simulationContext, List.of(new ConsoleGameLogHandler()));

		assertEvents(
				event -> event.isBeginCast() || event.isEffect(),
				at(0)
						.beginCast(player2, CHAIN_OF_THE_TWILIGHT_OWL)
						.effectApplied(CHAIN_OF_THE_TWILIGHT_OWL, player2, 1800 + 1000 * 3600)
						.beginCast(player3, EYE_OF_THE_NIGHT)
						.effectApplied(EYE_OF_THE_NIGHT, player3, 1800 + 1000 * 3600),
				at(180)
						.effectRemoved(CHAIN_OF_THE_TWILIGHT_OWL, player2)
						.effectRemoved(EYE_OF_THE_NIGHT, player3)
		);
	}
}
