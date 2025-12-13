package wow.simulator.simulation.spell.tbc.talent.priest;

import wow.simulator.simulation.spell.tbc.talent.TalentSimulationTest;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcPriestTalentSimulationTest extends TalentSimulationTest {
	@Override
	protected void beforeSetUp() {
		setSimulationParams(PRIEST, UNDEAD, TBC_P5);
	}
}
