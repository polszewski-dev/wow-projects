package wow.simulator.simulation.spell.tbc.talent.mage;

import wow.simulator.simulation.spell.tbc.talent.TalentSimulationTest;

import static wow.commons.model.character.CharacterClassId.MAGE;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcMageTalentSimulationTest extends TalentSimulationTest {
	@Override
	protected void beforeSetUp() {
		setSimulationParams(MAGE, UNDEAD, TBC_P5);
	}
}
