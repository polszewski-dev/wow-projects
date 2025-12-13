package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_WAVE;
import static wow.test.commons.TalentNames.PURIFICATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class PurificationTest extends TbcShamanTalentSimulationTest {
	/*
	Increases the effectiveness of your healing spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void healing_is_increased(int rank) {
		simulateTalent(PURIFICATION, rank, HEALING_WAVE);

		assertHealingIsIncreasedByPct(2 * rank);
	}
}
