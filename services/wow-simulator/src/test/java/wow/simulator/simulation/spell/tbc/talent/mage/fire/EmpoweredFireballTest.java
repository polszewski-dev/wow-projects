package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.EMPOWERED_FIREBALL;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class EmpoweredFireballTest extends TbcMageTalentSimulationTest {
	/*
	Your Fireball spell gains an additional 15% of your bonus spell damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spell_coefficient_is_increased(int rank) {
		simulateTalent(EMPOWERED_FIREBALL, rank, FIREBALL);

		assertDamageCoefficientIsIncreasedBy(3 * rank);
	}
}
