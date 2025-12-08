package wow.simulator.simulation.spell.tbc.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SEARING_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class SearingPainTest extends TbcWarlockSpellSimulationTest {
	/*
	Inflict searing pain on the enemy target, causing 270 to 320 Fire damage.  Causes a high amount of threat.
	 */

	@Test
	void success() {
		player.cast(SEARING_PAIN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SEARING_PAIN, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, SEARING_PAIN)
						.decreasedResource(205, MANA, player, SEARING_PAIN)
						.decreasedResource(295, HEALTH, target, SEARING_PAIN)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SEARING_PAIN, spellDamage);

		assertDamageDone(SEARING_PAIN_INFO, spellDamage);
	}
}
