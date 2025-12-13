package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.STARFIRE;
import static wow.test.commons.AbilityNames.WRATH;
import static wow.test.commons.TalentNames.FOCUSED_STARLIGHT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class FocusedStarlightTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the critical strike chance of your Wrath and Starfire spells by 4%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void wrath_crit_chance_is_increased(int rank) {
		simulateTalent(FOCUSED_STARLIGHT, rank, WRATH);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void starfire_crit_chance_is_increased(int rank) {
		simulateTalent(FOCUSED_STARLIGHT, rank, STARFIRE);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}
}
