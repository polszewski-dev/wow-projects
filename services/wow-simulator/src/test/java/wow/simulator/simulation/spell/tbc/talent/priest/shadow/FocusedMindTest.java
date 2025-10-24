package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.MIND_FLAY;
import static wow.test.commons.TalentNames.FOCUSED_MIND;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class FocusedMindTest extends TbcPriestSpellSimulationTest {
	/*
	Reduces the mana cost of your Mind Blast, Mind Control and Mind Flay spells by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mindBlastCostReduced(int rank) {
		enableTalent(FOCUSED_MIND, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertManaPaid(MIND_BLAST, player, MIND_BLAST_INFO.manaCost(), -5 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mindFlayCostReduced(int rank) {
		enableTalent(FOCUSED_MIND, rank);
		enableTalent(TalentNames.MIND_FLAY, 1);

		player.cast(MIND_FLAY);

		updateUntil(30);

		assertManaPaid(MIND_FLAY, player, MIND_FLAY_INFO.manaCost(), -5 * rank);
	}
}
