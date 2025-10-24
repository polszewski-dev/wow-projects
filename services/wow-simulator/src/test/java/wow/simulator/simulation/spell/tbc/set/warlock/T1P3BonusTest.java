package wow.simulator.simulation.spell.tbc.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.DRAIN_LIFE;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T1P3BonusTest extends TbcWarlockSpellSimulationTest {
	/*
    Health or Mana gained from Drain Life and Drain Mana increased by 15%.
	*/

	@Test
	void drainLifeHealthGainIsIncreased() {
		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertHealthGained(DRAIN_LIFE, player, DRAIN_LIFE_INFO.damage(totalSpellDamage), 15);
	}

	@Override
	protected void afterSetUp() {
		equip("Felheart Belt");
		equip("Felheart Bracers");
		equip("Felheart Gloves");
		setHealth(player, 1000);
	}
}