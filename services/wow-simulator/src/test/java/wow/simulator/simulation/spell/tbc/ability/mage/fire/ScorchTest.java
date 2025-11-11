package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SCORCH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ScorchTest extends TbcMageSpellSimulationTest {
	/*
	Scorch the enemy for 305 to 361 Fire damage.
	 */

	@Test
	void success() {
		player.cast(SCORCH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SCORCH, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, SCORCH)
						.decreasedResource(180, MANA, player, SCORCH)
						.decreasedResource(333, HEALTH, target, SCORCH)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SCORCH, spellDamage);

		assertDamageDone(SCORCH_INFO, spellDamage);
	}
}
