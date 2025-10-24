package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.TalentNames.EMPOWERED_CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class EmpoweredCorruptionTest extends TbcWarlockSpellSimulationTest {
	/*
	Your Corruption spell gains an additional 36% of your bonus spell damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void empoweredCorruption(int rank) {
		addSpBonus(100);

		enableTalent(EMPOWERED_CORRUPTION, rank);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(12 * rank, 100));
	}
}
