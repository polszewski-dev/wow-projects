package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.BACKLASH;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class BacklashTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases your critical strike chance with spells by an additional 3% and gives you a 25% chance when hit by a physical attack to reduce the cast time
	of your next Shadow Bolt or Incinerate spell by 100%. This effect lasts 8 sec and will not occur more than once every 8 seconds.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(BACKLASH, rank, SHADOW_BOLT);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
