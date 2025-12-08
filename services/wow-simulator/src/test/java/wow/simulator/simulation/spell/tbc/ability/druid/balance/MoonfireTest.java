package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MOONFIRE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class MoonfireTest extends TbcDruidSpellSimulationTest {
	/*
	Burns the enemy for 305 to 357 Arcane damage and then an additional 600 Arcane damage over 12 sec.
	 */

	@Test
	void success() {
		player.cast(MOONFIRE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MOONFIRE)
						.beginGcd(player)
						.endCast(player, MOONFIRE)
						.decreasedResource(495, MANA, player, MOONFIRE)
						.decreasedResource(331, HEALTH, target, MOONFIRE)
						.effectApplied(MOONFIRE, target, 12),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(150, HEALTH, target, MOONFIRE),
				at(6)
						.decreasedResource(150, HEALTH, target, MOONFIRE),
				at(9)
						.decreasedResource(150, HEALTH, target, MOONFIRE),
				at(12)
						.decreasedResource(150, HEALTH, target, MOONFIRE)
						.effectExpired(MOONFIRE, target)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(MOONFIRE, spellDamage);

		assertDamageDone(MOONFIRE_INFO, spellDamage);
	}
}
