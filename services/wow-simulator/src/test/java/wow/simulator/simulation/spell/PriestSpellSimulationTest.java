package wow.simulator.simulation.spell;

import wow.simulator.util.SpellInfos;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class PriestSpellSimulationTest extends SpellSimulationTest implements SpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = PRIEST;
		raceId = UNDEAD;
	}
}
