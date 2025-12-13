package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.FIRE_POWER;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class FirePowerTest extends TbcMageTalentSimulationTest {
	/*
	Increases the damage done by your Fire spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damage_is_increased(int rank) {
		simulateTalent(FIRE_POWER, rank, FIREBALL);

		assertDamageIsIncreasedByPct(2 * rank);
	}
}
