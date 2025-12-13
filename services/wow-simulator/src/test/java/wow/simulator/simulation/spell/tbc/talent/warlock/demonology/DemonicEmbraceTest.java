package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.TalentNames.DEMONIC_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicEmbraceTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases your total Stamina by 15% but reduces your total Spirit by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void stamina_is_increased(int rank) {
		assertStaminaIsIncreasedByPct(DEMONIC_EMBRACE, rank, 3 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spirit_is_reduced(int rank) {
		assertSpiritIsReducedByPct(DEMONIC_EMBRACE, rank, rank);
	}
}
