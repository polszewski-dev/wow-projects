package wow.simulator.simulation.spell.tbc.ability.priest.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SMITE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class SmiteTest extends TbcPriestSpellSimulationTest {
	/*
	Smite an enemy for 549 to 616 Holy damage.
	 */

	@Test
	void success() {
		player.cast(SMITE);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, SMITE, 2.5)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(2.5)
					.endCast(player, SMITE)
					.decreasedResource(385, MANA, player, SMITE)
					.decreasedResource(582, HEALTH, target, SMITE)
		);
	}

	@Test
	void damageDone() {
		player.cast(SMITE);

		updateUntil(30);

		assertDamageDone(SMITE, SMITE_INFO.damage());
	}
}
