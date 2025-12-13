package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class EarthShieldTest extends TbcShamanTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.EARTH_SHIELD, AbilityNames.EARTH_SHIELD);
	}
}
