package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T1P8BonusTest extends WarlockSpellSimulationTest {
	/*
    Mana cost of Shadow spells reduced by 15%.
	*/

	@Test
	void manaCostIsReduced() {
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertManaPaid(SHADOW_BOLT, player, SHADOW_BOLT_INFO.manaCost(), -15);
	}

	@Override
	protected void afterSetUp() {
		equip("Felheart Belt");
		equip("Felheart Bracers");
		equip("Felheart Gloves");
		equip("Felheart Pants");
		equip("Felheart Robes");
		equip("Felheart Shoulder Pads");
		equip("Felheart Horns");
		equip("Felheart Slippers");
	}
}