package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.AbilityId.CURSE_OF_THE_ELEMENTS;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-18
 */
class CurseOfTheElementsTest extends WarlockSpellSimulationTest {
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
					.endCast(player, CURSE_OF_THE_ELEMENTS)
					.decreasedResource(260, MANA, player, CURSE_OF_THE_ELEMENTS)
					.effectApplied(CURSE_OF_THE_ELEMENTS, target, 300)
					.beginGcd(player),
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
