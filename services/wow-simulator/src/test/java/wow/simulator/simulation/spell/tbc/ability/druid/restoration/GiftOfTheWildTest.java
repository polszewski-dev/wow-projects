package wow.simulator.simulation.spell.tbc.ability.druid.restoration;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.GIFT_OF_THE_WILD;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class GiftOfTheWildTest extends TbcDruidSpellSimulationTest {
	/*
	Gives the Gift of the Wild to the target's party, increasing armor by 340, all attributes by 14 and all resistances by 25 for 1 hour.
	 */

	@Test
	void success() {
		player.cast(GIFT_OF_THE_WILD, player2);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, GIFT_OF_THE_WILD)
						.beginGcd(player)
						.endCast(player, GIFT_OF_THE_WILD)
						.decreasedResource(1515, MANA, player, GIFT_OF_THE_WILD)
						.effectApplied(GIFT_OF_THE_WILD, player2, 3600)
						.effectApplied(GIFT_OF_THE_WILD, player3, 3600)
						.effectApplied(GIFT_OF_THE_WILD, player4, 3600),
				at(1.5)
						.endGcd(player),
				at(3600)
						.effectExpired(GIFT_OF_THE_WILD, player2)
						.effectExpired(GIFT_OF_THE_WILD, player3)
						.effectExpired(GIFT_OF_THE_WILD, player4)
		);
	}

	@Test
	void attributes_are_increased() {
		simulateBuffSpell(GIFT_OF_THE_WILD);

		assertBaseStatsIncreasedBy(14);
	}

	@Override
	protected void afterSetUp() {
		player2.getParty().add(player3, player4);
	}
}
