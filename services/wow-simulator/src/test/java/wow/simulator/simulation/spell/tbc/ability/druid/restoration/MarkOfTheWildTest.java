package wow.simulator.simulation.spell.tbc.ability.druid.restoration;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MARK_OF_THE_WILD;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class MarkOfTheWildTest extends TbcDruidSpellSimulationTest {
	/*
	Increases the friendly target's armor by 340, all attributes by 14 and all resistances by 25 for 30 min.
	 */

	@Test
	void success() {
		player.cast(MARK_OF_THE_WILD);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, MARK_OF_THE_WILD)
						.beginGcd(player)
						.endCast(player, MARK_OF_THE_WILD)
						.decreasedResource(565, MANA, player, MARK_OF_THE_WILD)
						.effectApplied(MARK_OF_THE_WILD, player, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(MARK_OF_THE_WILD, player)
		);
	}

	@Test
	void attributes_are_increased() {
		simulateBuffSpell(MARK_OF_THE_WILD);

		assertBaseStatsIncreasedBy(14);
	}
}
