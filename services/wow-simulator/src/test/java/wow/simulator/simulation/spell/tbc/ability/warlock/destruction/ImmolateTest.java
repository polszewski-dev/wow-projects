package wow.simulator.simulation.spell.tbc.ability.warlock.destruction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.IMMOLATE;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ImmolateTest extends TbcWarlockSpellSimulationTest {
	/*
	Burns the enemy for 332 Fire damage and then an additional 615 Fire damage over 15 sec.
	 */

	@Test
	void success() {
		player.cast(IMMOLATE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target, 15),
				at(5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(8)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(11)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(14)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(17)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(IMMOLATE, spellDamage);

		assertDamageDone(IMMOLATE_INFO, spellDamage);
	}
}
