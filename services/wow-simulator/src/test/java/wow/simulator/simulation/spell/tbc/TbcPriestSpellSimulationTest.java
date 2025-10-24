package wow.simulator.simulation.spell.tbc;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcPriestSpellSimulationTest extends SpellSimulationTest implements TbcSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = PRIEST;
		raceId = UNDEAD;
		phaseId = TBC_P5;
	}
}
