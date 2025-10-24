package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicSacrificeTest extends TbcWarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.DEMONIC_SACRIFICE, AbilityNames.DEMONIC_SACRIFICE);
	}
}
