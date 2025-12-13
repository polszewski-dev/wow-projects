package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.WRATH;
import static wow.test.commons.TalentNames.BALANCE_OF_POWER;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class BalanceOfPowerTest extends TbcDruidTalentSimulationTest {
	/*
	Increases your chance to hit with all spells and reduces the chance you'll be hit by spells by 4%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void hit_chance_is_increased(int rank) {
		simulateTalent(BALANCE_OF_POWER, rank, WRATH);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}
}
