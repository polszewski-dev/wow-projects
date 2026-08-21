package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.EMBRACE_OF_THE_DAWN;

/**
 * User: POlszewski
 * Date: 2026-08-20
 */
class EmbraceOfTheDawnTest extends TbcWarlockSpellSimulationTest {
	/*
	Use: All stats of nearby party members increased by 10 for 30 min. (1 Hour Cooldown)
	 */
	@Test
	void effect_is_applied_and_cooldown_is_triggered() {
		player.cast(EMBRACE_OF_THE_DAWN);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, EMBRACE_OF_THE_DAWN)
						.endCast(player, EMBRACE_OF_THE_DAWN)
						.cooldownStarted(player, EMBRACE_OF_THE_DAWN, 3600)
						.effectApplied(EMBRACE_OF_THE_DAWN, player, 1800),
				at(1800)
						.effectExpired(EMBRACE_OF_THE_DAWN, player),
				at(3600)
						.cooldownExpired(player, EMBRACE_OF_THE_DAWN)
		);
	}

	@Test
	void crit_pct_is_increased_for_other_party_members() {
		player.cast(EMBRACE_OF_THE_DAWN);

		updateUntil(10);

		baseline.resetEquipment();

		assertStatsAreIncreasedBy(player2, 10);
		assertStatsAreIncreasedBy(player3, 10);
		assertStatsAreIncreasedBy(player4, 10);
		assertStatsAreIncreasedBy(player5, 10);
	}

	@Override
	protected void afterSetUp() {
		equip(EMBRACE_OF_THE_DAWN);
	}
}
