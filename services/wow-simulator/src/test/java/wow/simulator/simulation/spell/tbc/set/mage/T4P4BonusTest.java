package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T4P4BonusTest extends TbcMageSpellSimulationTest {
	/*
	Reduces the cooldown on Presence of Mind by 24 sec, on Blast Wave by 4 sec, and on Ice Block by 40 sec.
	 */

	@Test
	void pom_cooldown_is_reduced() {
		enableTalent(PRESENCE_OF_MIND);

		player.cast(PRESENCE_OF_MIND);

		updateUntil(30);

		assertCooldownIsReducedBy(PRESENCE_OF_MIND_INFO, player, 24);
	}

	@Test
	void blast_wave_cooldown_is_reduced() {
		enableTalent(BLAST_WAVE);

		player.cast(BLAST_WAVE);

		updateUntil(30);

		assertCooldownIsReducedBy(BLAST_WAVE_INFO, player, 4);
	}

	@Test
	void ice_block_wave_cooldown_is_reduced() {
		player.cast(ICE_BLOCK);

		updateUntil(30);

		assertCooldownIsReducedBy(ICE_BLOCK_INFO, player, 40);
	}

	@Override
	protected void afterSetUp() {
		equip("Gloves of the Aldor");
		equip("Legwraps of the Aldor");
		equip("Pauldrons of the Aldor");
		equip("Vestments of the Aldor");
	}
}
