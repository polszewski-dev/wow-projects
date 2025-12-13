package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class FelDominationTest extends TbcWarlockTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.FEL_DOMINATION, AbilityNames.FEL_DOMINATION);
	}
}
