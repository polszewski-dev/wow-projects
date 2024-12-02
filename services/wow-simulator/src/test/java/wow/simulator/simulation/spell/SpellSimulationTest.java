package wow.simulator.simulation.spell;

import org.junit.jupiter.api.BeforeEach;
import wow.commons.model.item.ItemSource;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.EventCooldownId;
import wow.simulator.WowSimulatorSpringTest;

import static wow.commons.model.pve.PhaseId.TBC_P5;

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

	protected EventCooldownId itemProcCooldownId(String itemName) {
		var item = getItemRepository().getItem(itemName, TBC_P5).orElseThrow();

		return CooldownId.of(new ItemSource(item), 0);
	}

	protected EventCooldownId itemProcCooldownId(int itemId) {
		var item = getItemRepository().getItem(itemId, TBC_P5).orElseThrow();

		return CooldownId.of(new ItemSource(item), 0);
	}
}
