package wow.simulator.simulation.spell.tbc.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class T3P4BonusTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases damage caused by your Corruption by 12%.
	 */

	@Test
	void damageIsIncreased() {
		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(totalSpellDamage), 12);
	}

	@Override
	protected void afterSetUp() {
		equip("Plagueheart Belt");
		equip("Plagueheart Bindings");
		equip("Plagueheart Circlet");
		equip("Plagueheart Gloves");
	}
}
