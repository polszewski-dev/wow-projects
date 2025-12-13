package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.TalentNames.LIVING_SPIRIT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class LivingSpiritTest extends TbcDruidTalentSimulationTest {
	/*
	Increases your total Spirit by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spirit_is_increased(int rank) {
		assertSpiritIsIncreasedByPct(LIVING_SPIRIT, rank, 5 * rank);
	}
}
