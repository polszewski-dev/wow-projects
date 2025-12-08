package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ENTANGLING_ROOTS;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class EntanglingRootsTest extends TbcDruidSpellSimulationTest {
	/*
	Roots the target in place and causes 351 Nature damage over 27 sec. Damage caused may interrupt the effect. Only useable outdoors.
	 */

	@Test
	void success() {
		player.cast(ENTANGLING_ROOTS);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ENTANGLING_ROOTS, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, ENTANGLING_ROOTS)
						.decreasedResource(160, MANA, player, ENTANGLING_ROOTS)
						.effectApplied(ENTANGLING_ROOTS, target, 27)
						.endGcd(player),
				at(4.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(7.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(10.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(13.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(16.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(19.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(22.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(25.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS),
				at(28.5)
						.decreasedResource(39, HEALTH, target, ENTANGLING_ROOTS)
						.effectExpired(ENTANGLING_ROOTS, target)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(ENTANGLING_ROOTS, spellDamage);

		assertDamageDone(ENTANGLING_ROOTS_INFO, spellDamage);
	}
}
