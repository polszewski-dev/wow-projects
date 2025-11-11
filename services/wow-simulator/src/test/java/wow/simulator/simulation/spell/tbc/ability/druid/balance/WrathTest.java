package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.WRATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class WrathTest extends TbcDruidSpellSimulationTest {
	/*
	Causes 383 to 432 Nature damage to the target.
	 */

	@Test
	void success() {
		player.cast(WRATH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, WRATH, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, WRATH)
						.decreasedResource(255, MANA, player, WRATH)
						.decreasedResource(407, HEALTH, target, WRATH)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(WRATH, spellDamage);

		assertDamageDone(WRATH_INFO, spellDamage);
	}
}
