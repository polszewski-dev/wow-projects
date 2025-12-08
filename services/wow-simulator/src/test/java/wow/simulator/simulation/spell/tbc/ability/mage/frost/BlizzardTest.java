package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BLIZZARD;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class BlizzardTest extends TbcMageSpellSimulationTest {
	/*
	Ice shards pelt the target area doing (185 * 8) Frost damage over 8 sec.
	 */

	@Test
	void success() {
		player.cast(BLIZZARD);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, BLIZZARD)
						.beginGcd(player)
						.endCast(player, BLIZZARD)
						.decreasedResource(1645, MANA, player, BLIZZARD)
						.effectApplied(BLIZZARD, null, 8)
						.beginChannel(player, BLIZZARD, 8),
				at(1)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(3)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(4)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(5)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(6)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(7)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD),
				at(8)
						.decreasedResource(185, HEALTH, target, BLIZZARD)
						.decreasedResource(185, HEALTH, target2, BLIZZARD)
						.decreasedResource(185, HEALTH, target3, BLIZZARD)
						.decreasedResource(185, HEALTH, target4, BLIZZARD)
						.effectExpired(BLIZZARD, null)
						.endChannel(player, BLIZZARD)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(BLIZZARD, spellDamage);

		assertDamageDone(BLIZZARD_INFO, target, spellDamage);
		assertDamageDone(BLIZZARD_INFO, target2, spellDamage);
		assertDamageDone(BLIZZARD_INFO, target3, spellDamage);
		assertDamageDone(BLIZZARD_INFO, target4, spellDamage);
	}
}
