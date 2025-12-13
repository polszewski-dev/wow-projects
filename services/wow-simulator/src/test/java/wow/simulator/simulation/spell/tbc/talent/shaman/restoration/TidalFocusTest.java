package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_WAVE;
import static wow.test.commons.TalentNames.TIDAL_FOCUS;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class TidalFocusTest extends TbcShamanTalentSimulationTest {
	/*
	Reduces the mana cost of your healing spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void mana_cost_is_reduced(int rank) {
		simulateTalent(TIDAL_FOCUS, rank, HEALING_WAVE);

		assertManaCostIsReducedByPct(rank);
	}
}
