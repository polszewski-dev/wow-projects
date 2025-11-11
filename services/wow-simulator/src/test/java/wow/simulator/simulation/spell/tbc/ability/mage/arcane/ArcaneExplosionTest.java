package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ARCANE_EXPLOSION;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ArcaneExplosionTest extends TbcMageSpellSimulationTest {
	/*
	Causes an explosion of arcane magic around the caster, causing 377 to 407 Arcane damage to all targets within 10 yards.
	 */

	@Test
	void success() {
		player.cast(ARCANE_EXPLOSION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_EXPLOSION)
						.beginGcd(player)
						.endCast(player, ARCANE_EXPLOSION)
						.decreasedResource(545, MANA, player, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, target, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, target2, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, target3, ARCANE_EXPLOSION)
						.decreasedResource(392, HEALTH, target4, ARCANE_EXPLOSION),
				at(1.5)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(ARCANE_EXPLOSION, spellDamage);

		assertDamageDone(ARCANE_EXPLOSION_INFO, target, spellDamage);
		assertDamageDone(ARCANE_EXPLOSION_INFO, target2, spellDamage);
		assertDamageDone(ARCANE_EXPLOSION_INFO, target3, spellDamage);
		assertDamageDone(ARCANE_EXPLOSION_INFO, target4, spellDamage);
	}
}
