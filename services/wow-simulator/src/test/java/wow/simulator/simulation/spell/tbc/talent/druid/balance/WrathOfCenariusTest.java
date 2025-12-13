package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.STARFIRE;
import static wow.test.commons.AbilityNames.WRATH;
import static wow.test.commons.TalentNames.WRATH_OF_CENARIUS;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class WrathOfCenariusTest extends TbcDruidTalentSimulationTest {
	/*
	Your Starfire spell gains an additional 20% and your Wrath gains an additional 10% of your bonus damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void starfire_spell_coefficient_is_increased(int rank) {
		simulateTalent(WRATH_OF_CENARIUS, rank, STARFIRE);

		assertDamageCoefficientIsIncreasedBy(4 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void wrath_spell_coefficient_is_increased(int rank) {
		simulateTalent(WRATH_OF_CENARIUS, rank, WRATH);

		assertDamageCoefficientIsIncreasedBy(2 * rank);
	}
}
