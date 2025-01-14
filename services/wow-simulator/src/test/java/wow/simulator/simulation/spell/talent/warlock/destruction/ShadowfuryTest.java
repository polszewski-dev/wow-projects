package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShadowfuryTest extends WarlockSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentId.SHADOWFURY, AbilityId.SHADOWFURY);
	}
}
