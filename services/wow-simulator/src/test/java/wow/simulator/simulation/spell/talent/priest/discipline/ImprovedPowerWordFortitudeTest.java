package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.POWER_WORD_FORTITUDE;
import static wow.commons.model.talent.TalentId.IMPROVED_POWER_WORD_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ImprovedPowerWordFortitudeTest extends PriestSpellSimulationTest {
	/*
	Increases the effect of your Power Word: Fortitude and Prayer of Fortitude spells by 30%.
	 */

	@Disabled
	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void effectIsIncreased(int rank) {
		var staminaBefore = player.getStats().getStamina();

		enableTalent(IMPROVED_POWER_WORD_FORTITUDE, rank);

		player.cast(POWER_WORD_FORTITUDE);

		updateUntil(30);

		var staminaAfter = player.getStats().getStamina();

		assertThat(staminaAfter).isEqualTo(staminaBefore + increaseByPct(79, 15 * rank));
	}
}
