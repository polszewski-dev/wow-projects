package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class T3P4BonusTest extends WarlockSpellSimulationTest {
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
