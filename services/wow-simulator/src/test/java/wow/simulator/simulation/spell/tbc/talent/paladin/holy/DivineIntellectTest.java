package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.test.commons.TalentNames.DIVINE_INTELLECT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class DivineIntellectTest extends TbcPaladinTalentSimulationTest {
	/*
	Increases your total Intellect by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void intellect_is_increased(int rank) {
		assertIntellectIsIncreasedByPct(DIVINE_INTELLECT, rank, 2 * rank);
	}
}
