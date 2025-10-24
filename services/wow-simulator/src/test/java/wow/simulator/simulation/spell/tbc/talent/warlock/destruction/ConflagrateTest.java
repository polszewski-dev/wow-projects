package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ConflagrateTest extends TbcWarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.CONFLAGRATE, AbilityNames.CONFLAGRATE);
	}
}
