package wow.simulator.simulation.spell.tbc.talent.paladin.retribution;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;
import wow.test.commons.AbilityNames;
import wow.test.commons.TalentNames;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class SanctityAuraTest extends TbcPaladinTalentSimulationTest {
	@Test
	void talent_adds_spell() {
		assertEnablingTalentTeachesAbility(TalentNames.SANCTITY_AURA, AbilityNames.SANCTITY_AURA);
	}
}
