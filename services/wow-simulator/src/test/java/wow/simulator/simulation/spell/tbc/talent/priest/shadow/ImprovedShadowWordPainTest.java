package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_WORD_PAIN;
import static wow.test.commons.TalentNames.IMPROVED_SHADOW_WORD_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ImprovedShadowWordPainTest extends TbcPriestTalentSimulationTest {
	/*
	Increases the duration of your Shadow Word: Pain spell by 6 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void effect_duration_is_increased(int rank) {
		simulateTalent(IMPROVED_SHADOW_WORD_PAIN, rank, SHADOW_WORD_PAIN);

		assertEffectDurationIsIncreasedBy(3 * rank);
	}
}
