package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CHAIN_OF_THE_TWILIGHT_OWL;

/**
 * User: POlszewski
 * Date: 2026-08-20
 */
class ChainOfTheTwilightOwlTest extends TbcWarlockSpellSimulationTest {
	/*
	Use: Increases the spell critical hit chance of nearby party members by 2% for 30 min. (1 Hour Cooldown)
	 */
	@Test
	void effect_is_applied_and_cooldown_is_triggered() {
		player.cast(CHAIN_OF_THE_TWILIGHT_OWL);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, CHAIN_OF_THE_TWILIGHT_OWL)
						.endCast(player, CHAIN_OF_THE_TWILIGHT_OWL)
						.cooldownStarted(player, CHAIN_OF_THE_TWILIGHT_OWL, 3600)
						.effectApplied(CHAIN_OF_THE_TWILIGHT_OWL, player, 1800),
				at(1800)
						.effectExpired(CHAIN_OF_THE_TWILIGHT_OWL, player),
				at(3600)
						.cooldownExpired(player, CHAIN_OF_THE_TWILIGHT_OWL)
		);
	}

	@Test
	void crit_pct_is_increased_for_other_party_members() {
		player.cast(CHAIN_OF_THE_TWILIGHT_OWL);

		updateUntil(10);

		baseline.resetEquipment();

		assertSpellCritPctIsIncreasedBy(player2, 2);
		assertSpellCritPctIsIncreasedBy(player3, 2);
		assertSpellCritPctIsIncreasedBy(player4, 2);
		assertSpellCritPctIsIncreasedBy(player5, 2);
	}

	@Override
	protected void afterSetUp() {
		equip(CHAIN_OF_THE_TWILIGHT_OWL);
	}
}
