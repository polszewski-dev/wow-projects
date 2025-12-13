package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class InsectSwarmTest extends TbcDruidTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.INSECT_SWARM, AbilityNames.INSECT_SWARM);
	}
}
