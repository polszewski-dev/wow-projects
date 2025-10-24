package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DEATH_COIL;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class DeathCoilTest extends TbcWarlockSpellSimulationTest {
	/*
	Causes the enemy target to run in horror for 3 sec and causes 526 Shadow damage.
	The caster gains 100% of the damage caused in health.
	 */

	@Test
	void success() {
		player.cast(DEATH_COIL);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, DEATH_COIL)
					.beginGcd(player)
					.endCast(player, DEATH_COIL)
					.decreasedResource(600, MANA, player, DEATH_COIL)
					.cooldownStarted(player, DEATH_COIL, 120)
					.decreasedResource(526, HEALTH, target, DEATH_COIL)
					.increasedResource(526, HEALTH, player, DEATH_COIL),
			at(1.5)
					.endGcd(player)
		);
	}

	@Test
	void damageDone() {
		player.cast(DEATH_COIL);

		updateUntil(30);

		assertDamageDone(DEATH_COIL, DEATH_COIL_INFO.damage());
	}

	@Test
	void healthGained() {
		player.cast(DEATH_COIL);

		updateUntil(30);

		assertHealthGained(DEATH_COIL, player, DEATH_COIL_INFO.damage());
	}

	@Override
	protected void afterSetUp() {
		setHealth(player, 1000);
	}
}
