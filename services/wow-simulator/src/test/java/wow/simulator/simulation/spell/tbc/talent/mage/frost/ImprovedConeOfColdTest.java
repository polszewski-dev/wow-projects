package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.CONE_OF_COLD;
import static wow.test.commons.TalentNames.IMPROVED_CONE_OF_COLD;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedConeOfColdTest extends TbcMageTalentSimulationTest {
	/*
	Increases the damage dealt by your Cone of Cold spell by 35%.
	 */

	@ParameterizedTest
	@CsvSource({
			"1, 15",
			"2, 25",
			"3, 35"
	})
	void damage_is_increased(int rank, int pctIncrease) {
		simulateTalent(IMPROVED_CONE_OF_COLD, rank, CONE_OF_COLD);

		assertDamageIsIncreasedByPct(pctIncrease);
	}
}
