package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FROST_SHOCK;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FrostShockTest extends TbcShamanSpellSimulationTest {
	/*
	Instantly shocks the target with frost, causing 647 to 683 Frost damage and slowing movement speed to 50% of normal. Lasts 8 sec.
	Causes a high amount of threat.
	 */

	@Test
	void success() {
		player.cast(FROST_SHOCK);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FROST_SHOCK)
						.beginGcd(player)
						.endCast(player, FROST_SHOCK)
						.decreasedResource(525, MANA, player, FROST_SHOCK)
						.cooldownStarted(player, FROST_SHOCK, 6)
						.decreasedResource(665, HEALTH, target, FROST_SHOCK),
				at(1.5)
						.endGcd(player),
				at(6)
						.cooldownExpired(player, FROST_SHOCK)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FROST_SHOCK, spellDamage);

		assertDamageDone(FROST_SHOCK_INFO, spellDamage);
	}
}
