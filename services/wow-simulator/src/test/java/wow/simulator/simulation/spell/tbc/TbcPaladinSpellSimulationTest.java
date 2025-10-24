package wow.simulator.simulation.spell.tbc;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.PALADIN;
import static wow.commons.model.character.RaceId.BLOOD_ELF;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcPaladinSpellSimulationTest extends SpellSimulationTest implements TbcSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = PALADIN;
		raceId = BLOOD_ELF;
		phaseId = TBC_P5;
	}
}
