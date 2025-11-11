package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FROSTBOLT;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FrostboltTest extends TbcMageSpellSimulationTest {
	/*
	Launches a bolt of frost at the enemy, causing 630 to 680 Frost damage and slowing movement speed by 40% for 9 sec.
	 */

	@Test
	void success() {
		player.cast(FROSTBOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FROSTBOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, FROSTBOLT)
						.decreasedResource(345, MANA, player, FROSTBOLT)
						.decreasedResource(655, HEALTH, target, FROSTBOLT)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FROSTBOLT, spellDamage);

		assertDamageDone(FROSTBOLT_INFO, spellDamage);
	}
}
