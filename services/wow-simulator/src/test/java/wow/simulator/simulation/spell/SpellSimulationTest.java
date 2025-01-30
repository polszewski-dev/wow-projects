package wow.simulator.simulation.spell;

import org.junit.jupiter.api.BeforeEach;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.util.TestEventCollectingHandler;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public abstract class SpellSimulationTest extends WowSimulatorSpringTest {
	@BeforeEach
	void setUp() {
		beforeSetUp();

		setupTestObjects();

		handler = new TestEventCollectingHandler();

		simulation.addHandler(handler);

		simulation.add(player);
		simulation.add(target);

		afterSetUp();
	}

	protected void beforeSetUp() {}

	protected void afterSetUp() {}
}
