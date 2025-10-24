package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DarkPactTest extends TbcWarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.DARK_PACT, AbilityNames.DARK_PACT);
	}
}
