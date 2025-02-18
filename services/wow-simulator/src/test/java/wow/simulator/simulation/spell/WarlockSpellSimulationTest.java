package wow.simulator.simulation.spell;

import wow.simulator.util.SpellInfos;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class WarlockSpellSimulationTest extends SpellSimulationTest implements SpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = WARLOCK;
		raceId = ORC;
	}
}
