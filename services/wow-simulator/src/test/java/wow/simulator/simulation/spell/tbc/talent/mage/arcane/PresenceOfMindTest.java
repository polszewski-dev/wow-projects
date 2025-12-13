
package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class PresenceOfMindTest extends TbcMageTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.PRESENCE_OF_MIND, AbilityNames.PRESENCE_OF_MIND);
	}
}
