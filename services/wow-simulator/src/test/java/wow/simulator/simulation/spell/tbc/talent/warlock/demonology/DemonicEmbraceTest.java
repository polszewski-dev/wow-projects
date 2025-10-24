package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.TalentNames.DEMONIC_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicEmbraceTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases your total Stamina by 15% but reduces your total Spirit by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void staminaBonus(int rank) {
		int staminaBefore = player.getStats().getStamina();

		enableTalent(DEMONIC_EMBRACE, rank);

		int staminaAfter = player.getStats().getStamina();

		assertIsIncreasedByPct(staminaAfter, staminaBefore, 3 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spiritBonus(int rank) {
		int spiritBefore = player.getStats().getSpirit();

		enableTalent(DEMONIC_EMBRACE, rank);

		int spiritAfter = player.getStats().getSpirit();

		assertIsIncreasedByPct(spiritAfter, spiritBefore, -rank);
	}
}
