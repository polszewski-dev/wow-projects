package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.TalentNames.LUNAR_GUIDANCE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class LunarGuidanceTest extends TbcDruidTalentSimulationTest {
	/*
	Increases your spell damage and healing by 25% of your total Intellect.
	 */

	@ParameterizedTest
	@CsvSource({
			"1, 8",
			"2, 16",
			"3, 25"
	})
	void spell_power_is_increased(int rank, int ratio) {
		assertStatConversion(LUNAR_GUIDANCE, rank, StatSummary::getIntellect, StatSummary::getSpellPower, ratio);
	}
}
