package wow.simulator.simulation.spell.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.model.unit.Player;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.PRAYER_OF_FORTITUDE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class PrayerOfFortitudeTest extends PriestSpellSimulationTest {
	/*
	Power infuses the target's party, increasing their Stamina by 79 for 1 hour.
	 */

	@Test
	void success() {
		player.cast(PRAYER_OF_FORTITUDE, player1);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, PRAYER_OF_FORTITUDE)
						.beginGcd(player)
						.endCast(player, PRAYER_OF_FORTITUDE)
						.decreasedResource(1800, MANA, player, PRAYER_OF_FORTITUDE)
						.effectApplied(PRAYER_OF_FORTITUDE, player1, 3600)
						.effectApplied(PRAYER_OF_FORTITUDE, player2, 3600)
						.effectApplied(PRAYER_OF_FORTITUDE, player3, 3600),
				at(1.5)
						.endGcd(player),
				at(3600)
						.effectExpired(PRAYER_OF_FORTITUDE, player1)
						.effectExpired(PRAYER_OF_FORTITUDE, player2)
						.effectExpired(PRAYER_OF_FORTITUDE, player3)
		);
	}

	@Test
	void staminaIsIncreased() {
		var staminaBefore = player.getStats().getStamina();

		player.cast(PRAYER_OF_FORTITUDE);

		updateUntil(30);

		var staminaAfter = player.getStats().getStamina();

		assertThat(staminaAfter).isEqualTo(staminaBefore + 79);
	}

	Player player1;
	Player player2;
	Player player3;

	@Override
	protected void afterSetUp() {
		player1 = getNakedPlayer(WARLOCK, "Player1");
		player2 = getNakedPlayer(WARLOCK, "Player2");
		player3 = getNakedPlayer(WARLOCK, "Player3");

		var party = player1.getParty();

		party.add(player2);
		party.add(player3);

		simulation.add(player1);
		simulation.add(player2);
		simulation.add(player3);
	}
}
