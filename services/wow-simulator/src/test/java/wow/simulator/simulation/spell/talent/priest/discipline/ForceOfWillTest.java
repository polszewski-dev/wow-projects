package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.talent.TalentId.FORCE_OF_WILL;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ForceOfWillTest extends PriestSpellSimulationTest {
	/*
	Increases your spell damage by 5% and the critical strike chance of your offensive spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIsIncreased(int rank) {
		enableTalent(FORCE_OF_WILL, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertDamageDone(MIND_BLAST, MIND_BLAST_INFO.damage(), rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void critChangeIsIncreased(int rank) {
		var critBefore = player.getStats().getSpellCritPct();

		enableTalent(FORCE_OF_WILL, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertLastCritChance(critBefore + rank);
	}
}
