package wow.simulator.simulation.spell.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ImmolateTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
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
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target, 15),
				at(5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(8)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

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
						.spellResisted(player, IMMOLATE, target)
		);
	}

	@Test
	void interrupted() {
		player.cast(IMMOLATE);

		updateUntil(1);

		player.interruptCurrentAction();

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, 2)
						.beginGcd(player),
				at(1)
						.castInterrupted(player, IMMOLATE)
						.endGcd(player)
		);
	}
}
