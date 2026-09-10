package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.SpellSimulationTest;

import java.util.List;

import static wow.test.commons.AbilityNames.CHAIN_OF_THE_TWILIGHT_OWL;
import static wow.test.commons.AbilityNames.EYE_OF_THE_NIGHT;

/**
 * User: POlszewski
 * Date: 2026-08-21
 */
class NecklaceTest extends SpellSimulationTest {
	@Test
	void necklace_abilities_are_activated_during_buff_phase() {
		equip(player2, "Chain of the Twilight Owl");
		equip(player3, "Eye of the Night");

		var scenario = getScenario(120);

		scenario.execute(player.getRaid(), target, List.of(handler));

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
