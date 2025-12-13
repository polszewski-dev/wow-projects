package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.REGROWTH;
import static wow.test.commons.TalentNames.IMPROVED_REGROWTH;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedRegrowthTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the critical effect chance of your Regrowth spell by 50%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(IMPROVED_REGROWTH, rank, REGROWTH);

		assertCritChanceIsIncreasedByPct(10 * rank);
	}
}
