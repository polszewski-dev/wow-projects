package wow.simulator.simulation.spell.tbc.talent.shaman;

import wow.simulator.simulation.spell.tbc.talent.TalentSimulationTest;

import static wow.commons.model.character.CharacterClassId.SHAMAN;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcShamanTalentSimulationTest extends TalentSimulationTest {
	@Override
	protected void beforeSetUp() {
		setSimulationParams(SHAMAN, ORC, TBC_P5);
	}
}
