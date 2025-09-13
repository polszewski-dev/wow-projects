package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.BuffNames.FEL_ARMOR;
import static wow.test.commons.TalentNames.EMPOWERED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class EmpoweredCorruptionTest extends WarlockSpellSimulationTest {
	/*
	Your Corruption spell gains an additional 36% of your bonus spell damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void empoweredCorruption(int rank) {
		enableBuff(FEL_ARMOR, 2);

		enableTalent(EMPOWERED_CORRUPTION, rank);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(12 * rank, 100));
	}
}
