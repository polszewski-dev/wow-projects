package wow.simulator.simulation.spell.tbc.talent.druid;

import wow.simulator.simulation.spell.tbc.talent.TalentSimulationTest;

import static wow.commons.model.character.CharacterClassId.DRUID;
import static wow.commons.model.character.RaceId.TAUREN;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcDruidTalentSimulationTest extends TalentSimulationTest {
	@Override
	protected void beforeSetUp() {
		setSimulationParams(DRUID, TAUREN, TBC_P5);
	}
}
