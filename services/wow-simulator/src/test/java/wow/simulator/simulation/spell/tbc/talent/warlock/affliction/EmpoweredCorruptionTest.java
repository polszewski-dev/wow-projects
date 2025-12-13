package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.TalentNames.EMPOWERED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class EmpoweredCorruptionTest extends TbcWarlockTalentSimulationTest {
	/*
	Your Corruption spell gains an additional 36% of your bonus spell damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spell_coefficient_is_increased(int rank) {
		simulateTalent(EMPOWERED_CORRUPTION, rank, CORRUPTION);

		assertDamageCoefficientIsIncreasedBy(12 * rank);
	}
}
