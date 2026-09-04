package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.TalentNames.ENLIGHTENMENT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class EnlightementTest extends TbcPriestTalentSimulationTest {
	/*
	Increases your total Stamina, Intellect and Spirit by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void stamina_is_increased(int rank) {
		enableTalent(player, ENLIGHTENMENT, rank);

		assertStaminaIsIncreasedByPct(player, rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void intellect_is_increased(int rank) {
		enableTalent(player, ENLIGHTENMENT, rank);

		assertIntellectIsIncreasedByPct(player, rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spirit_is_increased(int rank) {
		enableTalent(player, ENLIGHTENMENT, rank);

		assertSpiritIsIncreasedByPct(player, rank);
	}
}
