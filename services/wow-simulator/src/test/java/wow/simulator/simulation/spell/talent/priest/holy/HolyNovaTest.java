package wow.simulator.simulation.spell.talent.priest.holy;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class HolyNovaTest extends PriestSpellSimulationTest {
	@Test
	void talentAddsSpell() {
		assertEnablingTalentTeachesAbility(TalentId.HOLY_NOVA, AbilityId.HOLY_NOVA);
	}
}
