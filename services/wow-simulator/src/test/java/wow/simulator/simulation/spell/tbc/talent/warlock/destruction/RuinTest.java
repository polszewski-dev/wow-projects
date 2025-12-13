package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.RUIN;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class RuinTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the critical strike damage bonus of your Destruction spells by 100%.
	 */

	@Test
	void crit_damage_bonus_is_increased() {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(RUIN, 1, SHADOW_BOLT);

		assertCritDamageBonusIsIncreasedByPct(100);
	}
}
