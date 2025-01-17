package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.talent.TalentId.IMPROVED_SHADOW_WORD_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ImprovedShadowWordPainTest extends PriestSpellSimulationTest {
	/*
	Increases the duration of your Shadow Word: Pain spell by 6 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void test(int rank) {
		enableTalent(IMPROVED_SHADOW_WORD_PAIN, rank);

		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEffectDuration(SHADOW_WORD_PAIN, target, 18 + 3 * rank);
	}
}
