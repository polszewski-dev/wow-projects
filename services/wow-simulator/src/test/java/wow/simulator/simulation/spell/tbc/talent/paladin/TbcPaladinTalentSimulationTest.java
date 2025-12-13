package wow.simulator.simulation.spell.tbc.talent.paladin;

import wow.simulator.simulation.spell.tbc.talent.TalentSimulationTest;

import static wow.commons.model.character.CharacterClassId.PALADIN;
import static wow.commons.model.character.RaceId.BLOOD_ELF;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcPaladinTalentSimulationTest extends TalentSimulationTest {
	@Override
	protected void beforeSetUp() {
		setSimulationParams(PALADIN, BLOOD_ELF, TBC_P5);
	}
}
