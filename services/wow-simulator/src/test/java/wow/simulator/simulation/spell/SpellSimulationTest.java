package wow.simulator.simulation.spell;

import org.junit.jupiter.api.BeforeEach;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.WowSimulatorSpringTest;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Unit;
import wow.simulator.util.TestEventCollectingHandler;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public abstract class SpellSimulationTest extends WowSimulatorSpringTest implements GameLogHandler {
	@BeforeEach
	void setUp() {
		beforeSetUp();

		setupTestObjects();

		handler = new TestEventCollectingHandler();

		simulation.addHandler(handler);
		simulation.addHandler(this);

		simulation.add(player);
		simulation.add(target);

		afterSetUp();
	}

	protected void beforeSetUp() {}

	protected void afterSetUp() {}

	protected int regeneratedHealth;
	protected int regeneratedMana;

	@Override
	public void increasedResource(ResourceType type, Spell spell, Unit target, int amount, int current, int previous, boolean crit) {
		if (target == player && spell == null) {
			switch (type) {
				case HEALTH -> regeneratedHealth += amount;
				case MANA -> regeneratedMana += amount;
			}
		}
	}
}
