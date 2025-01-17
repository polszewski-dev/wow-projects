package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.talent.TalentId.IMPROVED_VAMPIRIC_EMBRACE;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ImprovedVampiricEmbraceTest extends PriestSpellSimulationTest {
	/*
	Increases the percentage healed by Vampiric Embrace by an additional 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void test(int rank) {
		enableTalent(IMPROVED_VAMPIRIC_EMBRACE, rank);

		//todo
	}
}
