package wow.simulator.simulation.spell.tbc.ability.priest.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.GREATER_HEAL;

/**
 * User: POlszewski
 * Date: 2025-11-16
 */
class GreaterHealTest extends TbcPriestSpellSimulationTest {
	/*
	Heals the target for 1110 over 15 sec.
	 */

	@Test
	void success() {
		player2.setCurrentHealth(1);

		player.cast(GREATER_HEAL, player2);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, GREATER_HEAL, 3)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(3)
					.endCast(player, GREATER_HEAL)
					.decreasedResource(825, MANA, player, GREATER_HEAL)
					.increasedResource(2608, HEALTH, player2, GREATER_HEAL)
		);
	}

	@ParameterizedTest
	@MethodSource("spellHealingLevels")
	void healing_done(int healing) {
		simulateHealingSpell(GREATER_HEAL, healing);

		assertHealthGained(GREATER_HEAL_INFO, healing);
	}
}
