package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.STARFIRE;
import static wow.test.commons.AbilityNames.WRATH;
import static wow.test.commons.TalentNames.STARLIGHT_WRATH;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class StarlightWrathTest extends TbcDruidTalentSimulationTest {
	/*
	Reduces the cast time of your Wrath and Starfire spells by 0.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void wrath_cast_time_is_reduced(int rank) {
		simulateTalent(STARLIGHT_WRATH, rank, WRATH);

		assertCastTimeIsReducedBy(0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void starfire_cast_time_is_reduced(int rank) {
		simulateTalent(STARLIGHT_WRATH, rank, STARFIRE);

		assertCastTimeIsReducedBy(0.1 * rank);
	}
}
