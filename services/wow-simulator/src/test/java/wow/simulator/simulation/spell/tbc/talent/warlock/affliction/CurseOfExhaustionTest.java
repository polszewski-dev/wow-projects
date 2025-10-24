package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class CurseOfExhaustionTest extends TbcWarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.CURSE_OF_EXHAUSTION, AbilityNames.CURSE_OF_EXHAUSTION);
	}
}
