package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.ICE_SHARDS;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class IceShardsTest extends TbcMageTalentSimulationTest {
	/*
	Increases the critical strike damage bonus of your Frost spells by 100%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void crit_damage_bonus_is_increased(int rank) {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(ICE_SHARDS, rank, FROSTBOLT);

		assertCritDamageBonusIsIncreasedByPct(20 * rank);
	}
}
