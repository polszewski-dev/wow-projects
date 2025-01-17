package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class InnerFocusTest extends PriestSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentId.INNER_FOCUS, AbilityId.INNER_FOCUS);
	}
}
