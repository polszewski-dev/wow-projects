package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.talent.TalentId.MEDITATION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
@Disabled
class MeditationTest extends PriestSpellSimulationTest {
	/*
	Allows 30% of your mana regeneration to continue while casting.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void manaRegenerationContinues(int rank) {
		enableTalent(MEDITATION, rank);

		//todo
	}
}
