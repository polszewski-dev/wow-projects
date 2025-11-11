package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ICE_LANCE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class IceLanceTest extends TbcMageSpellSimulationTest {
	/*
	Deals 173 to 200 Frost damage to an enemy target. Causes triple damage against Frozen targets.
	 */

	@Test
	void success() {
		player.cast(ICE_LANCE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ICE_LANCE)
						.beginGcd(player)
						.endCast(player, ICE_LANCE)
						.decreasedResource(150, MANA, player, ICE_LANCE)
						.decreasedResource(186, HEALTH, target, ICE_LANCE),
				at(1.5)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(ICE_LANCE, spellDamage);

		assertDamageDone(ICE_LANCE_INFO, spellDamage);
	}
}
