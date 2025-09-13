package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.TalentNames.DARKNESS;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DarknessTest extends PriestSpellSimulationTest {
	/*
	Increases your Shadow spell damage by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIsIncreased(int rank) {
		enableTalent(DARKNESS, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertDamageDone(MIND_BLAST, MIND_BLAST_INFO.damage(), 2 * rank);
	}
}
