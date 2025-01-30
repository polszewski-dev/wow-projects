package wow.simulator.simulation.spell.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.talent.TalentId.SPIRITUAL_GUIDANCE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SpiritualGuidanceTest extends PriestSpellSimulationTest {
	/*
	Increases spell damage and healing by up to 25% of your total Spirit.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIsIncreased(int rank) {
		enableTalent(SPIRITUAL_GUIDANCE, rank);

		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		var totalSpirit = player.getStats().getSpirit();
		var totalSp = getPercentOf(5 * rank, totalSpirit);

		assertDamageDone(SHADOW_WORD_PAIN, SHADOW_WORD_PAIN_INFO.damage(totalSp));
	}
}
