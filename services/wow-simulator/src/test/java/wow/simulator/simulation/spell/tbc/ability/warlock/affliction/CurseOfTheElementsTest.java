package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.CURSE_OF_THE_ELEMENTS;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class CurseOfTheElementsTest extends TbcWarlockSpellSimulationTest {
	/*
	Curses the target for 5 min, reducing Arcane, Fire, Frost, and Shadow resistances by 88 and increasing Arcane, Fire,
	Frost, and Shadow damage taken by 10%.  Only one Curse per Warlock can be active on any one target.
	 */

	@Test
	void success() {
		player.cast(CURSE_OF_THE_ELEMENTS);

		updateUntil(300);

		assertEvents(
			at(0)
					.beginCast(player, CURSE_OF_THE_ELEMENTS)
					.beginGcd(player)
					.endCast(player, CURSE_OF_THE_ELEMENTS)
					.decreasedResource(260, MANA, player, CURSE_OF_THE_ELEMENTS)
					.effectApplied(CURSE_OF_THE_ELEMENTS, target, 300),
			at(1.5)
					.endGcd(player),
			at(300)
					.effectExpired(CURSE_OF_THE_ELEMENTS, target)
		);
	}

	@Test
	void damageIsIncreased() {
		player.cast(CURSE_OF_THE_ELEMENTS);
		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(), 10);
	}
}
