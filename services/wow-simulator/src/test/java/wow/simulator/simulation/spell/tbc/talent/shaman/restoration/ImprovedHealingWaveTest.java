package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_WAVE;
import static wow.test.commons.TalentNames.IMPROVED_HEALING_WAVE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedHealingWaveTest extends TbcShamanTalentSimulationTest {
	/*
	Reduces the casting time of your Healing Wave spell by 0.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void cast_time_is_reduced(int rank) {
		simulateTalent(IMPROVED_HEALING_WAVE, rank, HEALING_WAVE);

		assertCastTimeIsReducedBy(0.1 * rank);
	}
}
