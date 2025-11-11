package wow.simulator.simulation.spell.tbc.ability.paladin.protection;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BLESSING_OF_KINGS;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class BlessingOfKingsTest extends TbcPaladinSpellSimulationTest {
	/*
	Places a Blessing on the friendly target, increasing total stats by 10% for 10 min.
	Players may only have one Blessing on them per Paladin at any one time.
	 */

	@Test
	void success() {
		player.cast(BLESSING_OF_KINGS);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, BLESSING_OF_KINGS)
						.beginGcd(player)
						.endCast(player, BLESSING_OF_KINGS)
						.decreasedResource(236, MANA, player, BLESSING_OF_KINGS)
						.effectApplied(BLESSING_OF_KINGS, player, 600),
				at(1.5)
						.endGcd(player),
				at(600)
						.effectExpired(BLESSING_OF_KINGS, player)
		);
	}

	@Test
	void attributes_are_increased() {
		simulateBuffSpell(BLESSING_OF_KINGS);

		assertBaseStatsIncreasedByPct(10);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.BLESSING_OF_KINGS, 1);
	}
}
