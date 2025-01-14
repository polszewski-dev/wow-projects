package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.buff.BuffId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.talent.TalentId.EMPOWERED_CORRUPTION;

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
		enableBuff(BuffId.FEL_ARMOR, 2);

		enableTalent(EMPOWERED_CORRUPTION, rank);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, (int) (900 + (93.6 + 12 * rank)));
	}
}
