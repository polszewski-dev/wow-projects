package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.EXORCISM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ExorcismTest extends TbcPaladinSpellSimulationTest {
	/*
	Causes 626 to 698 Holy damage to an Undead or Demon target.
	 */

	@Test
	void success() {
		player.cast(EXORCISM);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, EXORCISM)
						.beginGcd(player)
						.endCast(player, EXORCISM)
						.decreasedResource(340, MANA, player, EXORCISM)
						.cooldownStarted(player, EXORCISM, 15)
						.decreasedResource(662, HEALTH, target, EXORCISM),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, EXORCISM)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(EXORCISM, spellDamage);

		assertDamageDone(EXORCISM_INFO, spellDamage);
	}
}
