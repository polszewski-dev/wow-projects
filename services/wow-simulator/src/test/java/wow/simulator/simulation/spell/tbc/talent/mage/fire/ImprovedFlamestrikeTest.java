package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FLAMESTRIKE;
import static wow.test.commons.TalentNames.IMPROVED_FLAMESTRIKE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedFlamestrikeTest extends TbcMageTalentSimulationTest {
	/*
	Increases the critical strike chance of your Flamestrike spell by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(IMPROVED_FLAMESTRIKE, rank, FLAMESTRIKE);

		assertCritChanceIsIncreasedByPct(5 * rank);
	}
}
