package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.PLAYING_WITH_FIRE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class PlayingWithFireTest extends TbcMageTalentSimulationTest {
	/*
	Increases all spell damage caused by 3% and all spell damage taken by 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void damage_is_increased(int rank) {
		simulateTalent(PLAYING_WITH_FIRE, rank, FIREBALL);

		assertDamageIsIncreasedByPct(rank);
	}
}
