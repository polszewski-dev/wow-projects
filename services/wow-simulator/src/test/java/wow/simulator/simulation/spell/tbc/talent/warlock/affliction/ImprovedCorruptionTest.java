package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCorruptionTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the casting time of your Corruption spell by 2 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void cast_time_is_reduced(int rank) {
		simulateTalent(IMPROVED_CORRUPTION, rank, CORRUPTION);

		assertCastTimeIsReducedBy(0.4 * rank);
	}
}
