package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.test.commons.AbilityNames.ARCANE_BLAST;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T5P2BonusTest extends TbcMageSpellSimulationTest {
	/*
	Increases the damage and mana cost of Arcane Blast by 20%.
	 */

	@Test
	void damage_is_increased() {
		player.cast(ARCANE_BLAST);

		updateUntil(30);

		var totalSpellDamage = baseline.getStats().getSpellDamage();

		assertDamageDone(ARCANE_BLAST_INFO, target, totalSpellDamage, 20);
	}

	@Test
	void mana_cost_is_increased() {
		player.cast(ARCANE_BLAST);

		updateUntil(30);

		assertManaPaid(ARCANE_BLAST_INFO, player, 20);
	}

	@Override
	protected void afterSetUp() {
		equip("Gloves of Tirisfal");
		equip("Leggings of Tirisfal");
	}
}
