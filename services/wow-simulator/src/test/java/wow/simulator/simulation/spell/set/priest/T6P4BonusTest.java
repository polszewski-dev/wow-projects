package wow.simulator.simulation.spell.set.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T6P4BonusTest extends PriestSpellSimulationTest {
	/*
    Increases the damage from your Mind Blast ability by 10%.
	*/

	@Test
	void test() {
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertDamageDone(MIND_BLAST, MIND_BLAST_INFO.damage(totalSpellDamage), 10);
	}

	@Override
	protected void afterSetUp() {
		equip("Handguards of Absolution");
		equip("Hood of Absolution");
		equip("Leggings of Absolution");
		equip("Shoulderpads of Absolution");
	}
}