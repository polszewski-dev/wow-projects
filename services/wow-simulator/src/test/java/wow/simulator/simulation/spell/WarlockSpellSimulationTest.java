package wow.simulator.simulation.spell;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class WarlockSpellSimulationTest extends SpellSimulationTest {
	@Override
	protected void beforeSetUp() {
		characterClassId = WARLOCK;
		raceId = ORC;
	}
}
