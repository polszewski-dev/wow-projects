package wow.simulator.simulation.spell.tbc.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HELLFIRE;

/**
 * User: POlszewski
 * Date: 2025-02-15
 */
class HellfireTest extends TbcWarlockSpellSimulationTest {
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
						.effectApplied(HELLFIRE, player, 15)
						.beginChannel(player, HELLFIRE, 15),
				at(1)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(3)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(4)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(5)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(6)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(7)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(8)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(9)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(10)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(11)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(12)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(13)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(14)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE),
				at(15)
						.decreasedResource(308, HEALTH, target, HELLFIRE)
						.decreasedResource(308, HEALTH, target2, HELLFIRE)
						.decreasedResource(308, HEALTH, target3, HELLFIRE)
						.decreasedResource(308, HEALTH, target4, HELLFIRE)
						.effectExpired(HELLFIRE, player)
						.endChannel(player, HELLFIRE)
		);
	}
}
