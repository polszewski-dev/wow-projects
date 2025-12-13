package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_TOUCH;
import static wow.test.commons.TalentNames.EMPOWERED_TOUCH;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class EmpoweredTouchTest extends TbcDruidTalentSimulationTest {
	/*
	Your Healing Touch spell gains an additional 20% of your bonus healing effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void spell_coefficient_is_increased(int rank) {
		simulateTalent(EMPOWERED_TOUCH, rank, HEALING_TOUCH);

		assertHealingCoefficientIsIncreasedBy(10 * rank);
	}
}
