package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HAMMER_OF_WRATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HammerOfWrathTest extends TbcPaladinSpellSimulationTest {
	/*
	Hurls a hammer that strikes an enemy for 672 to 742 Holy damage. Only usable on enemies that have 20% or less health.
	 */

	@Test
	void success() {
		player.cast(HAMMER_OF_WRATH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, HAMMER_OF_WRATH, 0.5)
						.beginGcd(player),
				at(0.5)
						.endCast(player, HAMMER_OF_WRATH)
						.decreasedResource(440, MANA, player, HAMMER_OF_WRATH)
						.cooldownStarted(player, HAMMER_OF_WRATH, 6)
						.decreasedResource(707, HEALTH, target, HAMMER_OF_WRATH),
				at(1.5)
						.endGcd(player),
				at(6.5)
						.cooldownExpired(player, HAMMER_OF_WRATH)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(HAMMER_OF_WRATH, spellDamage);

		assertDamageDone(HAMMER_OF_WRATH_INFO, spellDamage);
	}
}
