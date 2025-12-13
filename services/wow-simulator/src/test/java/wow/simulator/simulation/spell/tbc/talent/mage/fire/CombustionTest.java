
package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class CombustionTest extends TbcMageTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.COMBUSTION, AbilityNames.COMBUSTION);
	}
}
