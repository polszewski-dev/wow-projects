package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.CalcUtils.increaseByPct;
import static wow.test.commons.TalentNames.MENTAL_STRENGTH;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class MentalStrengthTest extends TbcPriestSpellSimulationTest {
	/*
	Increases your maximum mana by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void maxManaIsIncreased(int rank) {
		var maxManaBefore = player.getStats().getMaxMana();

		enableTalent(MENTAL_STRENGTH, rank);

		var maxManaAfter = player.getStats().getMaxMana();

		assertThat(maxManaAfter).isEqualTo(increaseByPct(maxManaBefore, 2 * rank));
	}
}
