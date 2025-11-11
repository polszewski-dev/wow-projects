package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FIREBALL;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FireballTest extends TbcMageSpellSimulationTest {
	/*
	Hurls a fiery ball that causes 717 to 913 Fire damage and an additional 84 Fire damage over 8 sec.
	 */

	@Test
	void success() {
		player.cast(FIREBALL);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FIREBALL, 3.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3.5)
						.endCast(player, FIREBALL)
						.decreasedResource(465, MANA, player, FIREBALL)
						.decreasedResource(815, HEALTH, false, target, FIREBALL)
						.effectApplied(FIREBALL, target, 8),
				at(5.5)
						.decreasedResource(21, HEALTH, target, FIREBALL),
				at(7.5)
						.decreasedResource(21, HEALTH, target, FIREBALL),
				at(9.5)
						.decreasedResource(21, HEALTH, target, FIREBALL),
				at(11.5)
						.decreasedResource(21, HEALTH, target, FIREBALL)
						.effectExpired(FIREBALL, target)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FIREBALL, spellDamage);

		assertDamageDone(FIREBALL_INFO, spellDamage);
	}
}
