package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.THICK_FELSTEEL_NECKLACE;

/**
 * User: POlszewski
 * Date: 2026-08-20
 */
class ThickFelsteelNecklaceTest extends TbcWarlockSpellSimulationTest {
	/*
	Use: Increases Stamina of nearby party members by 20 for 30 min. (1 Hour Cooldown)
	 */
	@Test
	void effect_is_applied_and_cooldown_is_triggered() {
		player.cast(THICK_FELSTEEL_NECKLACE);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, THICK_FELSTEEL_NECKLACE)
						.endCast(player, THICK_FELSTEEL_NECKLACE)
						.cooldownStarted(player, THICK_FELSTEEL_NECKLACE, 3600)
						.effectApplied(THICK_FELSTEEL_NECKLACE, player, 1800),
				at(1800)
						.effectExpired(THICK_FELSTEEL_NECKLACE, player),
				at(3600)
						.cooldownExpired(player, THICK_FELSTEEL_NECKLACE)
		);
	}

	@Test
	void crit_pct_is_increased_for_other_party_members() {
		player.cast(THICK_FELSTEEL_NECKLACE);

		updateUntil(10);

		baseline.resetEquipment();

		assertStaminaIsIncreasedBy(player2, 20);
		assertStaminaIsIncreasedBy(player3, 20);
		assertStaminaIsIncreasedBy(player4, 20);
		assertStaminaIsIncreasedBy(player5, 20);
	}

	@Override
	protected void afterSetUp() {
		equip(THICK_FELSTEEL_NECKLACE);
	}
}
