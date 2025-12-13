package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.REJUVENATION;
import static wow.test.commons.TalentNames.IMPROVED_REJUVENATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedRejuvenationTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the effect of your Rejuvenation spell by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void healing_is_increased(int rank) {
		simulateTalent(IMPROVED_REJUVENATION, rank, REJUVENATION);

		assertHealingIsIncreasedByPct(5 * rank);
	}
}
