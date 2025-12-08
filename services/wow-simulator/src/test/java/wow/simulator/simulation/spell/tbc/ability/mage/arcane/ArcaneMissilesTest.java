package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ARCANE_MISSILES;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ArcaneMissilesTest extends TbcMageSpellSimulationTest {
	/*
	Launches Arcane Missiles at the enemy, causing 286 Arcane damage every 1 sec for 5 sec.
	 */

	@Test
	void success() {
		player.cast(ARCANE_MISSILES);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_MISSILES)
						.beginGcd(player)
						.endCast(player, ARCANE_MISSILES)
						.decreasedResource(785, MANA, player, ARCANE_MISSILES)
						.effectApplied(ARCANE_MISSILES, target, 5)
						.beginChannel(player, ARCANE_MISSILES, 5),
				at(1)
						.decreasedResource(286, HEALTH, target, ARCANE_MISSILES),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(286, HEALTH, target, ARCANE_MISSILES),
				at(3)
						.decreasedResource(286, HEALTH, target, ARCANE_MISSILES),
				at(4)
						.decreasedResource(286, HEALTH, target, ARCANE_MISSILES),
				at(5)
						.decreasedResource(286, HEALTH, target, ARCANE_MISSILES)
						.effectExpired(ARCANE_MISSILES, target)
						.endChannel(player, ARCANE_MISSILES)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(ARCANE_MISSILES, spellDamage);

		assertDamageDone(ARCANE_MISSILES_INFO, spellDamage);
	}
}
