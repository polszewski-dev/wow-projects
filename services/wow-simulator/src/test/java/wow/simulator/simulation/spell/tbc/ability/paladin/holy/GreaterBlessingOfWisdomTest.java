package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.GREATER_BLESSING_OF_WISDOM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class GreaterBlessingOfWisdomTest extends TbcPaladinSpellSimulationTest {
	/*
	Gives all members of the raid or group that share the same class with the target the Greater Blessing of Wisdom,
	restoring 41 mana every 5 seconds for 30 min. Players may only have one Blessing on them per Paladin at any one time.
	 */

	@Test
	void success() {
		player.cast(GREATER_BLESSING_OF_WISDOM, player2);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, GREATER_BLESSING_OF_WISDOM)
						.beginGcd(player)
						.endCast(player, GREATER_BLESSING_OF_WISDOM)
						.decreasedResource(310, MANA, player, GREATER_BLESSING_OF_WISDOM)
						.effectApplied(GREATER_BLESSING_OF_WISDOM, player2, 1800)
						.effectApplied(GREATER_BLESSING_OF_WISDOM, player3, 1800)
						.effectApplied(GREATER_BLESSING_OF_WISDOM, player4, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(GREATER_BLESSING_OF_WISDOM, player2)
						.effectExpired(GREATER_BLESSING_OF_WISDOM, player3)
						.effectExpired(GREATER_BLESSING_OF_WISDOM, player4)
		);
	}

	@Test
	void mp5_is_increased() {
		simulateBuffSpell(GREATER_BLESSING_OF_WISDOM);

		assertMp5IncreasedBy(41);
	}

	@Override
	protected void afterSetUp() {
		player2.getParty().add(player3, player4);
	}
}
