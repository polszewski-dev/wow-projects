package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ForceOfNatureTest extends TbcDruidTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.FORCE_OF_NATURE, AbilityNames.FORCE_OF_NATURE);
	}
}
