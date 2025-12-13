
package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class IcyVeinsTest extends TbcMageTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.ICY_VEINS, AbilityNames.ICY_VEINS);
	}
}
