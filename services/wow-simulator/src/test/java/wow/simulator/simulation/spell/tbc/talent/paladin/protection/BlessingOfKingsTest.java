package wow.simulator.simulation.spell.tbc.talent.paladin.protection;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class BlessingOfKingsTest extends TbcPaladinTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.BLESSING_OF_KINGS, AbilityNames.BLESSING_OF_KINGS);
	}
}
