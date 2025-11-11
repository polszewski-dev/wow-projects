package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.POWER_WORD_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class PowerWordFortitudeTest extends TbcPriestSpellSimulationTest {
	/*
	Power infuses the target increasing their Stamina by 79 for 30 min.
	 */

	@Test
	void success() {
		player.cast(POWER_WORD_FORTITUDE);

		updateUntil(30 * 60);

		assertEvents(
				at(0)
						.beginCast(player, POWER_WORD_FORTITUDE)
						.beginGcd(player)
						.endCast(player, POWER_WORD_FORTITUDE)
						.decreasedResource(700, MANA, player, POWER_WORD_FORTITUDE)
						.effectApplied(POWER_WORD_FORTITUDE, player, 30 * 60),
				at(1.5)
						.endGcd(player),
				at(30 * 60)
						.effectExpired(POWER_WORD_FORTITUDE, player)
		);
	}

	@Test
	void stamina_is_increased() {
		simulateBuffSpell(POWER_WORD_FORTITUDE);

		assertStaminaIncreasedBy(79);
	}
}
