package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class CircleOfHealingTest extends TbcPriestTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.CIRCLE_OF_HEALING, AbilityNames.CIRCLE_OF_HEALING);
	}
}
