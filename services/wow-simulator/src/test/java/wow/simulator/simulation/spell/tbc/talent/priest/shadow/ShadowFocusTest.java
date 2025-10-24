package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.TalentNames.SHADOW_FOCUS;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ShadowFocusTest extends TbcPriestSpellSimulationTest {
	/*
	Reduces your target's chance to resist your Shadow spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void test(int rank) {
		enableTalent(SHADOW_FOCUS, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertLastHitChance(83 + 2 * rank);
	}
}
