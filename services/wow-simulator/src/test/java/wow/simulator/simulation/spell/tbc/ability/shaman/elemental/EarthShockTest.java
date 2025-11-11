package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.EARTH_SHOCK;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class EarthShockTest extends TbcShamanSpellSimulationTest {
	/*
	Instantly shocks the target with concussive force, causing 661 to 696 Nature damage.
	It also interrupts spellcasting and prevents any spell in that school from being cast for 2 sec.
	 */

	@Test
	void success() {
		player.cast(EARTH_SHOCK);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, EARTH_SHOCK)
						.beginGcd(player)
						.endCast(player, EARTH_SHOCK)
						.decreasedResource(535, MANA, player, EARTH_SHOCK)
						.cooldownStarted(player, EARTH_SHOCK, 6)
						.decreasedResource(678, HEALTH, target, EARTH_SHOCK),
				at(1.5)
						.endGcd(player),
				at(6)
						.cooldownExpired(player, EARTH_SHOCK)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(EARTH_SHOCK, spellDamage);

		assertDamageDone(EARTH_SHOCK_INFO, spellDamage);
	}
}
