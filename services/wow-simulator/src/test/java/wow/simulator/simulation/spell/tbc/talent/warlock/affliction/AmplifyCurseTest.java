package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class AmplifyCurseTest extends TbcWarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.AMPLIFY_CURSE, AbilityNames.AMPLIFY_CURSE);
	}
}
