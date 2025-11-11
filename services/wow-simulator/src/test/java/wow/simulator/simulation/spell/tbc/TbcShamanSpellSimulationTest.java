package wow.simulator.simulation.spell.tbc;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.SHAMAN;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcShamanSpellSimulationTest extends SpellSimulationTest implements TbcSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = SHAMAN;
		raceId = ORC;
		phaseId = TBC_P5;
	}
}
