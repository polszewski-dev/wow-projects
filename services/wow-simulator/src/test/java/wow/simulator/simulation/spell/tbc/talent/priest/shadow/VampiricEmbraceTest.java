package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class VampiricEmbraceTest extends TbcPriestSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.VAMPIRIC_EMBRACE, AbilityNames.VAMPIRIC_EMBRACE);
	}
}
