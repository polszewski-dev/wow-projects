package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class DivineIlluminationTest extends TbcPaladinTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.DIVINE_ILLUMINATION, AbilityNames.DIVINE_ILLUMINATION);
	}
}
