package wow.simulator.simulation.spell.vanilla;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.PALADIN;
import static wow.commons.model.character.RaceId.HUMAN;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class VanillaPaladinSpellSimulationTest extends SpellSimulationTest implements VanillaSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = PALADIN;
		raceId = HUMAN;
		phaseId = VANILLA_P6;
	}
}
