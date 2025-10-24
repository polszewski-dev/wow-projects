package wow.simulator.simulation.spell.tbc.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.IMMOLATE;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T25P3BonusTest extends TbcWarlockSpellSimulationTest {
	/*
    5% increased damage on your Immolate spell.
	*/

	@Test
	void damageIsIncreased() {
		player.cast(IMMOLATE);

		updateUntil(30);

		assertDamageDone(IMMOLATE, IMMOLATE_INFO.damage(totalSpellDamage), 5);
	}

	@Override
	protected void afterSetUp() {
		equip("Doomcaller's Circlet");
		equip("Doomcaller's Mantle");
		equip("Doomcaller's Robes");
	}
}