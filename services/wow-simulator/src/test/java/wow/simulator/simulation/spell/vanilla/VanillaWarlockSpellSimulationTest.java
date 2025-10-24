package wow.simulator.simulation.spell.vanilla;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class VanillaWarlockSpellSimulationTest extends SpellSimulationTest implements VanillaSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = WARLOCK;
		raceId = ORC;
		phaseId = VANILLA_P6;
	}
}
