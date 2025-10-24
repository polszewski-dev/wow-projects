package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.model.unit.Player;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.PRAYER_OF_SPIRIT;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class PrayerOfSpiritTest extends TbcPriestSpellSimulationTest {
	/*
	Power infuses the target's party, increasing their Spirit by 50 for 1 hour.
	 */

	@Test
	void success() {
		player.cast(PRAYER_OF_SPIRIT, player1);

		updateUntil(3600);

		assertEvents(
			at(0)
					.beginCast(player, PRAYER_OF_SPIRIT)
					.beginGcd(player)
					.endCast(player, PRAYER_OF_SPIRIT)
					.decreasedResource(1800, MANA, player, PRAYER_OF_SPIRIT)
					.effectApplied(PRAYER_OF_SPIRIT, player1, 3600)
					.effectApplied(PRAYER_OF_SPIRIT, player2, 3600)
					.effectApplied(PRAYER_OF_SPIRIT, player3, 3600),
			at(1.5)
					.endGcd(player),
			at(3600)
					.effectExpired(PRAYER_OF_SPIRIT, player1)
					.effectExpired(PRAYER_OF_SPIRIT, player2)
					.effectExpired(PRAYER_OF_SPIRIT, player3)
		);
	}

	@Test
	void spiritIsIncreased() {
		player.cast(PRAYER_OF_SPIRIT);

		updateUntil(30);

		var spiritBefore = statsAt(0).getSpirit();
		var spiritAfter = statsAt(1).getSpirit();

		assertThat(spiritAfter).isEqualTo(spiritBefore + 50);
	}

	Player player1;
	Player player2;
	Player player3;

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DIVINE_SPIRIT, 1);

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
