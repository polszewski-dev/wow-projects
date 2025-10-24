package wow.simulator.simulation.spell.tbc.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class T3P2BonusTest extends TbcWarlockSpellSimulationTest {
	/*
	Your Shadow Bolts now have a chance to heal you for 270 to 330.
	 */

	@Test
	void shadowBoltsHealPlayer() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(624, HEALTH, target, SHADOW_BOLT)
						.increasedResource(300, HEALTH, player, SHADOW_BOLT)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Plagueheart Belt");
		equip("Plagueheart Bindings");
		setHealth(player, 1000);
	}
}
