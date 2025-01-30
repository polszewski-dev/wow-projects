package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.spell.AbilityId.MIND_FLAY;
import static wow.commons.model.talent.TalentId.FOCUSED_MIND;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class FocusedMindTest extends PriestSpellSimulationTest {
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
		enableTalent(TalentId.MIND_FLAY, 1);

		player.cast(MIND_FLAY);

		updateUntil(30);

		assertManaPaid(MIND_FLAY, player, MIND_FLAY_INFO.manaCost(), -5 * rank);
	}
}
