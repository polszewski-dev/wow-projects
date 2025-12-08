package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FROST_NOVA;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FrostNovaTest extends TbcMageSpellSimulationTest {
	/*
	Blasts enemies near the caster for 100 to 113 Frost damage and freezes them in place for up to 8 sec. Damage caused may interrupt the effect.
	 */

	@Test
	void success() {
		player.cast(FROST_NOVA);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FROST_NOVA)
						.beginGcd(player)
						.endCast(player, FROST_NOVA)
						.decreasedResource(185, MANA, player, FROST_NOVA)
						.cooldownStarted(player, FROST_NOVA, 25)
						.decreasedResource(106, HEALTH, target, FROST_NOVA)
						.decreasedResource(106, HEALTH, target2, FROST_NOVA)
						.decreasedResource(106, HEALTH, target3, FROST_NOVA)
						.decreasedResource(106, HEALTH, target4, FROST_NOVA),
				at(1.5)
						.endGcd(player),
				at(25)
						.cooldownExpired(player, FROST_NOVA)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FROST_NOVA, spellDamage);

		assertDamageDone(FROST_NOVA_INFO, target, spellDamage);
		assertDamageDone(FROST_NOVA_INFO, target2, spellDamage);
		assertDamageDone(FROST_NOVA_INFO, target3, spellDamage);
		assertDamageDone(FROST_NOVA_INFO, target4, spellDamage);
	}
}
