package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BLESSING_OF_WISDOM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class BlessingOfWisdomTest extends TbcPaladinSpellSimulationTest {
	/*
	Places a Blessing on the friendly target, restoring 41 mana every 5 seconds for 10 min.
	Players may only have one Blessing on them per Paladin at any one time.
	 */

	@Test
	void success() {
		player.cast(BLESSING_OF_WISDOM);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, BLESSING_OF_WISDOM)
						.beginGcd(player)
						.endCast(player, BLESSING_OF_WISDOM)
						.decreasedResource(150, MANA, player, BLESSING_OF_WISDOM)
						.effectApplied(BLESSING_OF_WISDOM, player, 600),
				at(1.5)
						.endGcd(player),
				at(600)
						.effectExpired(BLESSING_OF_WISDOM, player)
		);
	}

	@Test
	void mp5_is_increased() {
		simulateBuffSpell(BLESSING_OF_WISDOM);

		assertMp5IncreasedBy(41);
	}
}
