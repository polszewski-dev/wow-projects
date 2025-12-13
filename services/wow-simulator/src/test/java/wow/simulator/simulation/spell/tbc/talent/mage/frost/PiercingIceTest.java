package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.PIERCING_ICE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class PiercingIceTest extends TbcMageTalentSimulationTest {
	/*
	Increases the damage done by your Frost spells by 6%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void damage_is_increased(int rank) {
		simulateTalent(PIERCING_ICE, rank, FROSTBOLT);

		assertDamageIsIncreasedByPct(2 * rank);
	}
}
