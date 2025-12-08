package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.CONE_OF_COLD;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ConeOfColdTest extends TbcMageSpellSimulationTest {
	/*
	Targets in a cone in front of the caster take 418 to 457 Frost damage and are slowed by 50% for 8 sec.
	 */

	@Test
	void success() {
		player.cast(CONE_OF_COLD);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CONE_OF_COLD)
						.beginGcd(player)
						.endCast(player, CONE_OF_COLD)
						.decreasedResource(645, MANA, player, CONE_OF_COLD)
						.cooldownStarted(player, CONE_OF_COLD, 10)
						.decreasedResource(437, HEALTH, target, CONE_OF_COLD)
						.decreasedResource(437, HEALTH, target2, CONE_OF_COLD)
						.decreasedResource(437, HEALTH, target3, CONE_OF_COLD)
						.decreasedResource(437, HEALTH, target4, CONE_OF_COLD),
				at(1.5)
						.endGcd(player),
				at(10)
						.cooldownExpired(player, CONE_OF_COLD)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(CONE_OF_COLD, spellDamage);

		assertDamageDone(CONE_OF_COLD_INFO, target, spellDamage);
		assertDamageDone(CONE_OF_COLD_INFO, target2, spellDamage);
		assertDamageDone(CONE_OF_COLD_INFO, target3, spellDamage);
		assertDamageDone(CONE_OF_COLD_INFO, target4, spellDamage);
	}
}
