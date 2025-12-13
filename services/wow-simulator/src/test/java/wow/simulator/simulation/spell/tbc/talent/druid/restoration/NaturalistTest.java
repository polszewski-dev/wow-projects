package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_TOUCH;
import static wow.test.commons.TalentNames.NATURALIST;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class NaturalistTest extends TbcDruidTalentSimulationTest {
	/*
	Reduces the cast time of your Healing Touch spell by 0.5 sec and increases the damage you deal with physical attacks in all forms by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void healing_touch_cast_time_is_reduced(int rank) {
		simulateTalent(NATURALIST, rank, HEALING_TOUCH);

		assertCastTimeIsReducedBy(0.1 * rank);
	}
}
