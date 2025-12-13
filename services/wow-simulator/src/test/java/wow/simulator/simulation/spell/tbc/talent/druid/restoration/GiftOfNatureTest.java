package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_TOUCH;
import static wow.test.commons.TalentNames.GIFT_OF_NATURE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class GiftOfNatureTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the effect of all healing spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void healing_is_increased(int rank) {
		simulateTalent(GIFT_OF_NATURE, rank, HEALING_TOUCH);

		assertHealingIsIncreasedByPct(2 * rank);
	}
}
