package wow.simulator.simulation.spell;

import org.junit.jupiter.api.BeforeEach;
import wow.simulator.WowSimulatorSpringTest;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public abstract class SpellSimulationTest extends WowSimulatorSpringTest {
	@BeforeEach
	public void setUp() {
		setupTestObjects();

		handler = new EventCollectingHandler();

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);
	}
}
