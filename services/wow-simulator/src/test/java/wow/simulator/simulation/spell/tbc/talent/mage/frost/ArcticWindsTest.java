package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.ARCTIC_WINDS;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ArcticWindsTest extends TbcMageTalentSimulationTest {
	/*
	Increases all Frost damage you cause by 5% and reduces the chance melee and ranged attacks will hit you by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damage_is_increased(int rank) {
		simulateTalent(ARCTIC_WINDS, rank, FROSTBOLT);

		assertDamageIsIncreasedByPct(rank);
	}
}
