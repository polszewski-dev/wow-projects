package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.VENGEANCE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class VengeanceTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the critical strike damage bonus of your Starfire, Moonfire, and Wrath spells by 100%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void starfire_crit_damage_bonus_is_increased(int rank) {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(VENGEANCE, rank, STARFIRE);

		assertCritDamageBonusIsIncreasedByPct(20 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void moonfire_crit_damage_bonus_is_increased(int rank) {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(VENGEANCE, rank, MOONFIRE);

		assertCritDamageBonusIsIncreasedByPct(20 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void wrath_crit_damage_bonus_is_increased(int rank) {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(VENGEANCE, rank, WRATH);

		assertCritDamageBonusIsIncreasedByPct(20 * rank);
	}
}
