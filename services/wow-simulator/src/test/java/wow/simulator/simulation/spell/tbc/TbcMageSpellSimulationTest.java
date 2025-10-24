package wow.simulator.simulation.spell.tbc;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.MAGE;
import static wow.commons.model.character.RaceId.TROLL;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcMageSpellSimulationTest extends SpellSimulationTest implements TbcSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = MAGE;
		raceId = TROLL;
		phaseId = TBC_P5;
	}
}
