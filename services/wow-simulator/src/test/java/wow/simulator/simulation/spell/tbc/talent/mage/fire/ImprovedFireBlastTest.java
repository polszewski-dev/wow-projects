package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIRE_BLAST;
import static wow.test.commons.TalentNames.IMPROVED_FIRE_BLAST;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedFireBlastTest extends TbcMageTalentSimulationTest {
	/*
	Reduces the cooldown of your Fire Blast spell by 1.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void cooldown_is_reduced(int rank) {
		simulateTalent(IMPROVED_FIRE_BLAST, rank, FIRE_BLAST);

		assertCooldownIsReducedBy(0.5 * rank);
	}
}
