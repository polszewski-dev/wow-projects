package wow.simulator.simulation.spell.tbc.ability.priest.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HOLY_FIRE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class HolyFireTest extends TbcPriestSpellSimulationTest {
	/*
	Consumes the enemy in Holy flames that cause 426 to 537 Holy damage and an additional 165 Holy damage over 10 sec.
	 */

	@Test
	void success() {
		player.cast(HOLY_FIRE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HOLY_FIRE, 3.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3.5)
						.endCast(player, HOLY_FIRE)
						.decreasedResource(290, MANA, player, HOLY_FIRE)
						.decreasedResource(481, HEALTH, target, HOLY_FIRE)
						.effectApplied(HOLY_FIRE, target, 10),
				at(5.5)
						.decreasedResource(33, HEALTH, target, HOLY_FIRE),
				at(7.5)
						.decreasedResource(33, HEALTH, target, HOLY_FIRE),
				at(9.5)
						.decreasedResource(33, HEALTH, target, HOLY_FIRE),
				at(11.5)
						.decreasedResource(33, HEALTH, target, HOLY_FIRE),
				at(13.5)
						.decreasedResource(33, HEALTH, target, HOLY_FIRE)
						.effectExpired(HOLY_FIRE, target)
		);
	}

	@Test
	void damageDone() {
		player.cast(HOLY_FIRE);

		updateUntil(30);

		assertDamageDone(HOLY_FIRE, HOLY_FIRE_INFO.damage());
	}
}
