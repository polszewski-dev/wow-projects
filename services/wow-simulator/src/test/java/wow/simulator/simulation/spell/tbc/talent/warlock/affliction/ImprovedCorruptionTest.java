package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.TalentNames.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCorruptionTest extends TbcWarlockSpellSimulationTest {
	/*
	Reduces the casting time of your Corruption spell by 2 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void improvedCorruption(int rank) {
		enableTalent(IMPROVED_CORRUPTION, rank);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertCastTime(CORRUPTION, CORRUPTION_INFO.baseCastTime() - 0.4 * rank);
	}
}
