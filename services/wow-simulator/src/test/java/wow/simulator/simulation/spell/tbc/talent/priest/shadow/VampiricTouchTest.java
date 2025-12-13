package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class VampiricTouchTest extends TbcPriestTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.VAMPIRIC_TOUCH, AbilityNames.VAMPIRIC_TOUCH);
	}
}
