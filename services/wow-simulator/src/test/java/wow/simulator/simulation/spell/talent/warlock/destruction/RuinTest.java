package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.RUIN;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class RuinTest extends WarlockSpellSimulationTest {
	/*
	Increases the critical strike damage bonus of your Destruction spells by 100%.
	 */

	@Test
	void critDamageBonusIncreased() {
		critsOnlyOnFollowingRolls(0);

		enableTalent(RUIN, 1);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertDamageDone(SHADOW_BOLT, SHADOW_BOLT_INFO.damage(), 100);
	}
}
