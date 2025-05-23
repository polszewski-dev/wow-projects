package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.*;
import static wow.commons.model.talent.TalentId.IMPROVED_VAMPIRIC_EMBRACE;
import static wow.simulator.util.CalcUtils.getPercentOf;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ImprovedVampiricEmbraceTest extends PriestSpellSimulationTest {
	/*
	Increases the percentage healed by Vampiric Embrace by an additional 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healthGainedFromMindBlast(int rank) {
		enableTalent(IMPROVED_VAMPIRIC_EMBRACE, rank);

		player.cast(VAMPIRIC_EMBRACE);
		player.cast(MIND_BLAST);

		updateUntil(60);

		assertHealthGained(VAMPIRIC_EMBRACE, player, getPercentOf(15 + 5 * rank, MIND_BLAST_INFO.damage()));
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healthGainedFromShadowWordPain(int rank) {
		enableTalent(IMPROVED_VAMPIRIC_EMBRACE, rank);

		player.cast(VAMPIRIC_EMBRACE);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(60);

		for (int tickNo = 0; tickNo < SHADOW_WORD_PAIN_INFO.numTicks(); ++tickNo) {
			assertHealthGained(tickNo, VAMPIRIC_EMBRACE, player, getPercentOf(15 + 5 * rank, SHADOW_WORD_PAIN_INFO.tickDamage()));
		}
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.VAMPIRIC_EMBRACE, 1);
	}
}
