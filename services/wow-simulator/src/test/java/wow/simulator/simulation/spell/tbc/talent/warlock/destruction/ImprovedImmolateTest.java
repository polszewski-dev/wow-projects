package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.IMMOLATE;
import static wow.test.commons.TalentNames.IMPROVED_IMMOLATE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedImmolateTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the initial damage of your Immolate spell by 25%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void direct_damage_is_increased(int rank) {
		simulateTalent(IMPROVED_IMMOLATE, rank, IMMOLATE);

		assertDirectDamageIsIncreasedByPct(5 * rank);
	}
}
