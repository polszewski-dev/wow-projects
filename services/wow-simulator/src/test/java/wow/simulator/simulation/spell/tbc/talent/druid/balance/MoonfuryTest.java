package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.MOONFURY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class MoonfuryTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the damage done by your Starfire, Moonfire and Wrath spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void starfire_damage_is_increased(int rank) {
		simulateTalent(MOONFURY, rank, STARFIRE);

		assertDamageIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void moonfire_damage_is_increased(int rank) {
		simulateTalent(MOONFURY, rank, MOONFIRE);

		assertDamageIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void wrath_damage_is_increased(int rank) {
		simulateTalent(MOONFURY, rank, WRATH);

		assertDamageIsIncreasedByPct(2 * rank);
	}
}
