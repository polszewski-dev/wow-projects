package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.IMPROVED_VAMPIRIC_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ImprovedVampiricEmbraceTest extends TbcPriestSpellSimulationTest {
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

		var totalHealing = getPercentOf(15 + 5 * rank, MIND_BLAST_INFO.damage());

		assertHealthGained(VAMPIRIC_EMBRACE, player, totalHealing);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healthGainedFromShadowWordPain(int rank) {
		enableTalent(IMPROVED_VAMPIRIC_EMBRACE, rank);

		player.cast(VAMPIRIC_EMBRACE);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(60);

		var totalHealing = getPercentOf(15 + 5 * rank, SHADOW_WORD_PAIN_INFO.damage());

		assertHealthGained(VAMPIRIC_EMBRACE, player, totalHealing);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.VAMPIRIC_EMBRACE, 1);
		setHealth(player, 1000);
	}
}
