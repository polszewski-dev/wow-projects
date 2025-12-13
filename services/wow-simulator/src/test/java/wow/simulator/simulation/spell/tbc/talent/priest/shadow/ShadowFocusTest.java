package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.TalentNames.SHADOW_FOCUS;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ShadowFocusTest extends TbcPriestTalentSimulationTest {
	/*
	Reduces your target's chance to resist your Shadow spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void hit_chance_is_increased(int rank) {
		simulateTalent(SHADOW_FOCUS, rank, MIND_BLAST);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}
}
