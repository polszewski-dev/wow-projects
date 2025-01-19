package wow.simulator.simulation.spell.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.AbilityId.POWER_WORD_FORTITUDE;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class PowerWordFortitudeTest extends PriestSpellSimulationTest {
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
						.endCast(player, POWER_WORD_FORTITUDE)
						.decreasedResource(700, MANA, player, POWER_WORD_FORTITUDE)
						.effectApplied(POWER_WORD_FORTITUDE, player, 30 * 60)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(30 * 60)
						.effectExpired(POWER_WORD_FORTITUDE, player)
		);
	}

	@Test
	void staminaIsIncreased() {
		var staminaBefore = player.getStats().getStamina();

		player.cast(POWER_WORD_FORTITUDE);

		updateUntil(30);

		var staminaAfter = player.getStats().getStamina();

		assertThat(staminaAfter).isEqualTo(staminaBefore + 79);
	}
}
