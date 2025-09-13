package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.IMMOLATE;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T6P2BonusTest extends WarlockSpellSimulationTest {
	/*
    Each time one of your Corruption or Immolate spells deals periodic damage, you heal 70 health.
	*/

	@Test
	void corruptionTicksHealCaster() {
		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(5)
						.decreasedResource(167, HEALTH, target, CORRUPTION)
						.increasedResource(70, HEALTH, player, CORRUPTION),
				at(8)
						.decreasedResource(167, HEALTH, target, CORRUPTION)
						.increasedResource(70, HEALTH, player, CORRUPTION),
				at(11)
						.decreasedResource(167, HEALTH, target, CORRUPTION)
						.increasedResource(70, HEALTH, player, CORRUPTION),
				at(14)
						.decreasedResource(167, HEALTH, target, CORRUPTION)
						.increasedResource(70, HEALTH, player, CORRUPTION),
				at(17)
						.decreasedResource(167, HEALTH, target, CORRUPTION)
						.increasedResource(70, HEALTH, player, CORRUPTION),
				at(20)
						.decreasedResource(167, HEALTH, target, CORRUPTION)
						.increasedResource(70, HEALTH, player, CORRUPTION)
						.effectExpired(CORRUPTION, target)
		);
	}

	@Test
	void immolateTicksHealCaster() {
		player.cast(IMMOLATE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(352, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target, 15),
				at(5)
						.decreasedResource(136, HEALTH, target, IMMOLATE)
						.increasedResource(70, HEALTH, player, IMMOLATE),
				at(8)
						.decreasedResource(137, HEALTH, target, IMMOLATE)
						.increasedResource(70, HEALTH, player, IMMOLATE),
				at(11)
						.decreasedResource(137, HEALTH, target, IMMOLATE)
						.increasedResource(70, HEALTH, player, IMMOLATE),
				at(14)
						.decreasedResource(137, HEALTH, target, IMMOLATE)
						.increasedResource(70, HEALTH, player, IMMOLATE),
				at(17)
						.decreasedResource(137, HEALTH, target, IMMOLATE)
						.increasedResource(70, HEALTH, player, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@Override
	protected void afterSetUp() {
		equip("Gloves of the Malefic");
		equip("Hood of the Malefic");
	}
}