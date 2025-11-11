package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.PRAYER_OF_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class PrayerOfFortitudeTest extends TbcPriestSpellSimulationTest {
	/*
	Power infuses the target's party, increasing their Stamina by 79 for 1 hour.
	 */

	@Test
	void success() {
		player.cast(PRAYER_OF_FORTITUDE, player2);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, PRAYER_OF_FORTITUDE)
						.beginGcd(player)
						.endCast(player, PRAYER_OF_FORTITUDE)
						.decreasedResource(1800, MANA, player, PRAYER_OF_FORTITUDE)
						.effectApplied(PRAYER_OF_FORTITUDE, player2, 3600)
						.effectApplied(PRAYER_OF_FORTITUDE, player3, 3600)
						.effectApplied(PRAYER_OF_FORTITUDE, player4, 3600),
				at(1.5)
						.endGcd(player),
				at(3600)
						.effectExpired(PRAYER_OF_FORTITUDE, player2)
						.effectExpired(PRAYER_OF_FORTITUDE, player3)
						.effectExpired(PRAYER_OF_FORTITUDE, player4)
		);
	}

	@Test
	void stamina_is_increased() {
		simulateBuffSpell(PRAYER_OF_FORTITUDE);

		assertStaminaIncreasedBy(79);
	}

	@Override
	protected void afterSetUp() {
		player2.getParty().add(player3, player4);
	}
}
