package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.DEVASTATION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DevastationTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the critical strike chance of your Destruction spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(DEVASTATION, rank, SHADOW_BOLT);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
