package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.talent.TalentId.IMPROVED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCorruptionTest extends WarlockSpellSimulationTest {
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
