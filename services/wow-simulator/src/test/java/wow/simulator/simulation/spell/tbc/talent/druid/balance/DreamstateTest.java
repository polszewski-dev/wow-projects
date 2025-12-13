package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.TalentNames.DREAMSTATE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class DreamstateTest extends TbcDruidTalentSimulationTest {
	/*
	Regenerate mana equal to 10% of your Intellect every 5 sec, even while casting.
	 */

	@ParameterizedTest
	@CsvSource({
			"1, 4",
			"2, 7",
			"3, 10"
	})
	void mp5_is_increased(int rank, int ratio) {
		assertStatConversion(DREAMSTATE, rank, StatSummary::getIntellect, StatSummary::getInterruptedManaRegen, ratio);
	}
}
