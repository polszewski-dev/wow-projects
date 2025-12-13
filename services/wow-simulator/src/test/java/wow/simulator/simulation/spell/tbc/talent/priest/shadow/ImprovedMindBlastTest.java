package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.TalentNames.IMPROVED_MIND_BLAST;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ImprovedMindBlastTest extends TbcPriestTalentSimulationTest {
	/*
	Reduces the cooldown of your Mind Blast spell by 2.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void cooldown_is_reduced(int rank) {
		simulateTalent(IMPROVED_MIND_BLAST, rank, MIND_BLAST);

		assertCooldownIsReducedBy(0.5 * rank);
	}
}
