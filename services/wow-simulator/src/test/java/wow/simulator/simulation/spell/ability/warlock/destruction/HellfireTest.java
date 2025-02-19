package wow.simulator.simulation.spell.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.HELLFIRE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
class HellfireTest extends WarlockSpellSimulationTest {
	/*
	Ignites the area surrounding the caster, causing 308 Fire damage to himself and 308 Fire damage to all nearby enemies every 1 sec.  Lasts 15 sec.
	 */

	@Test
	void success() {
		player.cast(HELLFIRE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HELLFIRE)
						.beginGcd(player)
						.endCast(player, HELLFIRE)
						.decreasedResource(1665, MANA, player, HELLFIRE)
						.effectApplied(HELLFIRE, null, 15)
						.beginChannel(player, HELLFIRE, 15),
				at(1)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(3)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(4)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(5)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(6)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(7)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(8)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(9)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(10)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(11)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(12)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(13)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(14)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE),
				at(15)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target1, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.effectExpired(HELLFIRE, null)
						.endChannel(player, HELLFIRE)
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
