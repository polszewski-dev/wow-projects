package wow.simulator.simulation.spell.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SOUL_FIRE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class SoulFireTest extends WarlockSpellSimulationTest {
	/*
	Burn the enemy's soul, causing 1003 to 1257 Fire damage.
	 */

	@Test
	void success() {
		player.cast(SOUL_FIRE);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, SOUL_FIRE, 6)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(6)
					.endCast(player, SOUL_FIRE)
					.decreasedResource(250, MANA, player, SOUL_FIRE)
					.cooldownStarted(player, SOUL_FIRE, 60)
					.decreasedResource(1130, HEALTH, target, SOUL_FIRE)
		);
	}

	@Test
	void damageDone() {
		player.cast(SOUL_FIRE);

		updateUntil(30);

		assertDamageDone(SOUL_FIRE, SOUL_FIRE_INFO.damage());
	}
}
