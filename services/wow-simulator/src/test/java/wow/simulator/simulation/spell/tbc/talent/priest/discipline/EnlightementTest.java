package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.CalcUtils.increaseByPct;
import static wow.test.commons.TalentNames.ENLIGHTENMENT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class EnlightementTest extends TbcPriestSpellSimulationTest {
	/*
	Increases your total Stamina, Intellect and Spirit by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void staminaIsIncreased(int rank) {
		var staminaBefore = player.getStats().getStamina();

		enableTalent(ENLIGHTENMENT, rank);

		var staminaAfter = player.getStats().getStamina();

		assertThat(staminaAfter).isEqualTo(increaseByPct(staminaBefore, rank));
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void intellectIsIncreased(int rank) {
		var intellectBefore = player.getStats().getIntellect();

		enableTalent(ENLIGHTENMENT, rank);

		var intellectAfter = player.getStats().getIntellect();

		assertThat(intellectAfter).isEqualTo(increaseByPct(intellectBefore, rank));
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spiritIsIncreased(int rank) {
		var spiritBefore = player.getStats().getSpirit();

		enableTalent(ENLIGHTENMENT, rank);

		var spiritAfter = player.getStats().getSpirit();

		assertThat(spiritAfter).isEqualTo(increaseByPct(spiritBefore, rank));
	}
}
