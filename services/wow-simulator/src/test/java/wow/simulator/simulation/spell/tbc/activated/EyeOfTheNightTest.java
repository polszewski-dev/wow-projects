package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.EYE_OF_THE_NIGHT;

/**
 * User: POlszewski
 * Date: 2026-08-19
 */
class EyeOfTheNightTest extends TbcWarlockSpellSimulationTest {
	/*
	Use: Increases spell damage by up to 34 for all nearby party members.  Lasts 30 min. (1 Hour Cooldown)
	 */
	@Test
	void effect_is_applied_and_cooldown_is_triggered() {
		player.cast(EYE_OF_THE_NIGHT);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, EYE_OF_THE_NIGHT)
						.endCast(player, EYE_OF_THE_NIGHT)
						.cooldownStarted(player, EYE_OF_THE_NIGHT, 3600)
						.effectApplied(EYE_OF_THE_NIGHT, player, 1800),
				at(1800)
						.effectExpired(EYE_OF_THE_NIGHT, player),
				at(3600)
						.cooldownExpired(player, EYE_OF_THE_NIGHT)
		);
	}

	@Test
	void sp_is_increased_for_other_party_members() {
		player.cast(EYE_OF_THE_NIGHT);

		updateUntil(10);

		baseline.resetEquipment();

		assertSpellDamageIsIncreasedBy(player2, 34);
		assertSpellDamageIsIncreasedBy(player3, 34);
		assertSpellDamageIsIncreasedBy(player4, 34);
		assertSpellDamageIsIncreasedBy(player5, 34);
	}

	@Override
	protected void afterSetUp() {
		equip(EYE_OF_THE_NIGHT);
	}
}
