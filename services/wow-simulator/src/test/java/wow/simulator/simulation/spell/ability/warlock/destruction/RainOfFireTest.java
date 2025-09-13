package wow.simulator.simulation.spell.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.RAIN_OF_FIRE;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
class RainOfFireTest extends WarlockSpellSimulationTest {
	/*
	Calls down a fiery rain to burn enemies in the area of effect for (304 * 4) Fire damage over 8 sec.
	 */

	@Test
	void success() {
		player.cast(RAIN_OF_FIRE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, RAIN_OF_FIRE)
						.beginGcd(player)
						.endCast(player, RAIN_OF_FIRE)
						.decreasedResource(1480, MANA, player, RAIN_OF_FIRE)
						.effectApplied(RAIN_OF_FIRE, null, 8)
						.beginChannel(player, RAIN_OF_FIRE, 8),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(304, HEALTH, target, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target1, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target2, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target3, RAIN_OF_FIRE),
				at(4)
						.decreasedResource(304, HEALTH, target, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target1, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target2, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target3, RAIN_OF_FIRE),
				at(6)
						.decreasedResource(304, HEALTH, target, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target1, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target2, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target3, RAIN_OF_FIRE),
				at(8)
						.decreasedResource(304, HEALTH, target, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target1, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target2, RAIN_OF_FIRE)
						.decreasedResource(304, HEALTH, target3, RAIN_OF_FIRE)
						.effectExpired(RAIN_OF_FIRE, null)
						.endChannel(player, RAIN_OF_FIRE)
		);
	}

	Unit target1;
	Unit target2;
	Unit target3;

	@Override
	protected void afterSetUp() {
		target1 = getEnemy("Target1");
		target2 = getEnemy("Target2");
		target3 = getEnemy("Target3");

		simulation.add(target1);
		simulation.add(target2);
		simulation.add(target3);
	}
}
