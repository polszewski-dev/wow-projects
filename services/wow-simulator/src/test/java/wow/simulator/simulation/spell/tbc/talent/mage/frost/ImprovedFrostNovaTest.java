package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROST_NOVA;
import static wow.test.commons.TalentNames.IMPROVED_FROST_NOVA;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedFrostNovaTest extends TbcMageTalentSimulationTest {
	/*
	Reduces the cooldown of your Frost Nova spell by 4 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void cooldown_is_reduced(int rank) {
		simulateTalent(IMPROVED_FROST_NOVA, rank, FROST_NOVA);

		assertCooldownIsReducedBy(2 * rank);
	}
}
