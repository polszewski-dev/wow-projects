package wow.simulator.simulation.spell.tbc.ability.priest.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SMITE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class SmiteTest extends TbcPriestSpellSimulationTest {
	/*
	Smite an enemy for 549 to 616 Holy damage.
	 */

	@Test
	void success() {
		player.cast(SMITE);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, SMITE, 2.5)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(2.5)
					.endCast(player, SMITE)
					.decreasedResource(385, MANA, player, SMITE)
					.decreasedResource(582, HEALTH, target, SMITE)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SMITE, spellDamage);

		assertDamageDone(SMITE_INFO, spellDamage);
	}
}
