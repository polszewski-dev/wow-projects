package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.TalentNames.MENTAL_STRENGTH;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class MentalStrengthTest extends TbcPriestTalentSimulationTest {
	/*
	Increases your maximum mana by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void max_mana_is_increased(int rank) {
		assertMaxManaIsIncreasedByPct(MENTAL_STRENGTH, rank, 2 * rank);
	}
}
