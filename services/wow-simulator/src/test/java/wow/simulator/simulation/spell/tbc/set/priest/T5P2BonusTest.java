package wow.simulator.simulation.spell.tbc.set.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SHADOW_WORD_DEATH;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T5P2BonusTest extends TbcPriestSpellSimulationTest {
	/*
	Each time you cast an offensive spell, there is a chance your next spell will cost 150 less mana.
	*/

	@Test
	void secondSpellHasCostReduced() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_WORD_DEATH);
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertManaPaid(SHADOW_WORD_DEATH, player, SHADOW_WORD_DEATH_INFO.manaCost());
		assertManaPaid(MIND_BLAST, player, MIND_BLAST_INFO.manaCost() - 150);
	}

	@Override
	protected void afterSetUp() {
		equip("Handguards of the Avatar");
		equip("Hood of the Avatar");
	}
}