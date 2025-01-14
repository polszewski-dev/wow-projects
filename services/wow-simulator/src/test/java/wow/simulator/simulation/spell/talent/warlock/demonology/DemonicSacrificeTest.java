package wow.simulator.simulation.spell.talent.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicSacrificeTest extends WarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentId.DEMONIC_SACRIFICE, AbilityId.DEMONIC_SACRIFICE);
	}
}
