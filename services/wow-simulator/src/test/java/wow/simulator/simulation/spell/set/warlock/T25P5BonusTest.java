package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T25P5BonusTest extends WarlockSpellSimulationTest {
	/*
    Reduces the mana cost of Shadow Bolt by 15%.
	*/

	@Test
	void manaCostIsReduced() {
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertManaPaid(SHADOW_BOLT, player, SHADOW_BOLT_INFO.manaCost(), -15);
	}

	@Override
	protected void afterSetUp() {
		equip("Doomcaller's Circlet");
		equip("Doomcaller's Footwraps");
		equip("Doomcaller's Mantle");
		equip("Doomcaller's Robes");
		equip("Doomcaller's Trousers");
	}
}