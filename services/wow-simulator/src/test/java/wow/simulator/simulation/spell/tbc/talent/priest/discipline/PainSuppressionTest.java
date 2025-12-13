package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class PainSuppressionTest extends TbcPriestTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.PAIN_SUPPRESSION, AbilityNames.PAIN_SUPPRESSION);
	}
}
