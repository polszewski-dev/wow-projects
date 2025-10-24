package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.CalcUtils.increaseByPct;
import static wow.test.commons.AbilityNames.POWER_WORD_FORTITUDE;
import static wow.test.commons.TalentNames.IMPROVED_POWER_WORD_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ImprovedPowerWordFortitudeTest extends TbcPriestSpellSimulationTest {
	/*
	Increases the effect of your Power Word: Fortitude and Prayer of Fortitude spells by 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void effectIsIncreased(int rank) {
		enableTalent(IMPROVED_POWER_WORD_FORTITUDE, rank);

		player.cast(POWER_WORD_FORTITUDE);

		updateUntil(30);

		var staminaBefore = statsAt(0).getStamina();
		var staminaAfter = statsAt(1).getStamina();

		assertThat(staminaAfter).isEqualTo(staminaBefore + increaseByPct(79, 15 * rank));
	}
}
