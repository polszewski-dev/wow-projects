package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.STARFIRE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class StarfireTest extends TbcDruidSpellSimulationTest {
	/*
	Causes 550 to 647 Arcane damage to the target.
	 */

	@Test
	void success() {
		player.cast(STARFIRE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, STARFIRE, 3.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3.5)
						.endCast(player, STARFIRE)
						.decreasedResource(370, MANA, player, STARFIRE)
						.decreasedResource(598, HEALTH, target, STARFIRE)

		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(STARFIRE, spellDamage);

		assertDamageDone(STARFIRE_INFO, spellDamage);
	}
}
