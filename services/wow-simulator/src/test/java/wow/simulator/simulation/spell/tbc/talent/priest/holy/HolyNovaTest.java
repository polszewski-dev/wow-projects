package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class HolyNovaTest extends TbcPriestSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentNames.HOLY_NOVA, AbilityNames.HOLY_NOVA);
	}
}
