package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.MOONFIRE;
import static wow.test.commons.TalentNames.IMPROVED_MOONFIRE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedMoonfireTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the damage and critical strike chance of your Moonfire spell by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damage_is_increased(int rank) {
		simulateTalent(IMPROVED_MOONFIRE, rank, MOONFIRE);

		assertDamageIsIncreasedByPct(5 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void crit_is_increased(int rank) {
		simulateTalent(IMPROVED_MOONFIRE, rank, MOONFIRE);

		assertCritChanceIsIncreasedByPct(5 * rank);
	}
}
