package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

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
		player.cast(PRAYER_OF_SPIRIT, player2);

		updateUntil(3600);

		assertEvents(
			at(0)
					.beginCast(player, PRAYER_OF_SPIRIT)
					.beginGcd(player)
					.endCast(player, PRAYER_OF_SPIRIT)
					.decreasedResource(1800, MANA, player, PRAYER_OF_SPIRIT)
					.effectApplied(PRAYER_OF_SPIRIT, player2, 3600)
					.effectApplied(PRAYER_OF_SPIRIT, player3, 3600)
					.effectApplied(PRAYER_OF_SPIRIT, player4, 3600),
			at(1.5)
					.endGcd(player),
			at(3600)
					.effectExpired(PRAYER_OF_SPIRIT, player2)
					.effectExpired(PRAYER_OF_SPIRIT, player3)
					.effectExpired(PRAYER_OF_SPIRIT, player4)
		);
	}

	@Test
	void spirit_is_increased() {
		simulateBuffSpell(PRAYER_OF_SPIRIT);

		assertSpiritIncreasedBy(50);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DIVINE_SPIRIT, 1);

		player2.getParty().add(player3, player4);
	}
}
