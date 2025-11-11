package wow.simulator.simulation.spell.tbc.ability.paladin.protection;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.GREATER_BLESSING_OF_KINGS;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class GreaterBlessingOfKingsTest extends TbcPaladinSpellSimulationTest {
	/*
	Gives all members of the raid or group that share the same class with the target the Greater Blessing of Kings,
	increasing total stats by 10% for 30 min. Players may only have one Blessing on them per Paladin at any one time.
	 */

	@Test
	void success() {
		player.cast(GREATER_BLESSING_OF_KINGS, player2);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, GREATER_BLESSING_OF_KINGS)
						.beginGcd(player)
						.endCast(player, GREATER_BLESSING_OF_KINGS)
						.decreasedResource(473, MANA, player, GREATER_BLESSING_OF_KINGS)
						.effectApplied(GREATER_BLESSING_OF_KINGS, player2, 1800)
						.effectApplied(GREATER_BLESSING_OF_KINGS, player3, 1800)
						.effectApplied(GREATER_BLESSING_OF_KINGS, player4, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(GREATER_BLESSING_OF_KINGS, player2)
						.effectExpired(GREATER_BLESSING_OF_KINGS, player3)
						.effectExpired(GREATER_BLESSING_OF_KINGS, player4)
		);
	}

	@Override
	protected void afterSetUp() {
		player2.getParty().add(player3, player4);
	}

	@Test
	void attributes_are_increased() {
		simulateBuffSpell(GREATER_BLESSING_OF_KINGS);

		assertBaseStatsIncreasedByPct(10);
	}
}
