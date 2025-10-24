package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.CURSE_OF_DOOM;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class CurseOfDoomTest extends TbcWarlockSpellSimulationTest {
	/*
	Curses the target with impending doom, causing 4200 Shadow damage after 1 min.
	If the target dies from this damage, there is a chance that a Doomguard will be summoned.  Cannot be cast on players.
	 */

	@Test
	void success() {
		player.cast(CURSE_OF_DOOM);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, CURSE_OF_DOOM)
						.beginGcd(player)
						.endCast(player, CURSE_OF_DOOM)
						.decreasedResource(380, MANA, player, CURSE_OF_DOOM)
						.cooldownStarted(player, CURSE_OF_DOOM, 60)
						.effectApplied(CURSE_OF_DOOM, target, 60),
				at(1.5)
						.endGcd(player),
				at(60)
						.cooldownExpired(player, CURSE_OF_DOOM)
						.decreasedResource(4200, HEALTH, target, CURSE_OF_DOOM)
						.effectExpired(CURSE_OF_DOOM, target)
		);
	}

	@Test
	void damageDone() {
		player.cast(CURSE_OF_DOOM);

		updateUntil(60);

		assertDamageDone(CURSE_OF_DOOM, CURSE_OF_DOOM_INFO.damage());
	}
}
