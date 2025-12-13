package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.REJUVENATION;
import static wow.test.commons.TalentNames.EMPOWERED_REJUVENATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class EmpoweredRejuvenationTest extends TbcDruidTalentSimulationTest {
	/*
	The bonus healing effects of your healing over time spells is increased by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spell_coefficient_is_increased(int rank) {
		simulateTalent(EMPOWERED_REJUVENATION, rank, REJUVENATION);

		assertHealingCoefficientIsIncreasedBy(4 * rank);
	}
}
