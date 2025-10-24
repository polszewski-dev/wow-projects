package wow.simulator.simulation.spell.vanilla;

import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.character.CharacterClassId.DRUID;
import static wow.commons.model.character.RaceId.TAUREN;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class VanillaDruidSpellSimulationTest extends SpellSimulationTest implements VanillaSpellInfos {
	@Override
	protected void beforeSetUp() {
		characterClassId = DRUID;
		raceId = TAUREN;
		phaseId = VANILLA_P6;
	}
}
