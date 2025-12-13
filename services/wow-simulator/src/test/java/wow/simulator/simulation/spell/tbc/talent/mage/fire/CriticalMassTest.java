package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.CRITICAL_MASS;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class CriticalMassTest extends TbcMageTalentSimulationTest {
	/*
	Increases the critical strike chance of your Fire spells by 6%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(CRITICAL_MASS, rank, FIREBALL);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}
}
