package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.MIND_FLAY;
import static wow.test.commons.TalentNames.FOCUSED_MIND;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class FocusedMindTest extends TbcPriestTalentSimulationTest {
	/*
	Reduces the mana cost of your Mind Blast, Mind Control and Mind Flay spells by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mind_blast_mana_cost_is_reduced(int rank) {
		simulateTalent(FOCUSED_MIND, rank, MIND_BLAST);

		assertManaCostIsReducedByPct(5 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mind_flay_ana_cost_is_reduced(int rank) {
		simulateTalent(FOCUSED_MIND, rank, MIND_FLAY);

		assertManaCostIsReducedByPct(5 * rank);
	}
}
