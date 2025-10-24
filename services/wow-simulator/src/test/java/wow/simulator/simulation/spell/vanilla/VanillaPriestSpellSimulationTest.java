package wow.simulator.simulation.spell.vanilla;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class VanillaPriestSpellSimulationTest extends SpellSimulationTest implements VanillaSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = PRIEST;
		raceId = UNDEAD;
		phaseId = VANILLA_P6;
	}
}
