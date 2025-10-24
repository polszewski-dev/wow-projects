package wow.simulator.simulation.spell.vanilla;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.SHAMAN;
import static wow.commons.model.character.RaceId.TROLL;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class VanillaShamanSpellSimulationTest extends SpellSimulationTest implements VanillaSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = SHAMAN;
		raceId = TROLL;
		phaseId = VANILLA_P6;
	}
}
