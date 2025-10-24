package wow.simulator.simulation.spell.tbc;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.DRUID;
import static wow.commons.model.character.RaceId.TAUREN;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class TbcDruidSpellSimulationTest extends SpellSimulationTest implements TbcSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = DRUID;
		raceId = TAUREN;
		phaseId = TBC_P5;
	}
}
