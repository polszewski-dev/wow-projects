package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.POWER_WORD_FORTITUDE;
import static wow.test.commons.TalentNames.IMPROVED_POWER_WORD_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ImprovedPowerWordFortitudeTest extends TbcPriestTalentSimulationTest {
	/*
	Increases the effect of your Power Word: Fortitude and Prayer of Fortitude spells by 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void stamina_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_POWER_WORD_FORTITUDE, rank, POWER_WORD_FORTITUDE);

		assertStatBonusIsIncreasedByPct(StatSummary::getStamina, 15 * rank);
	}
}
