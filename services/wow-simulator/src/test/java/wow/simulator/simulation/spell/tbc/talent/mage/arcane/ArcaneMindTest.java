package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.TalentNames.ARCANE_MIND;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ArcaneMindTest extends TbcMageTalentSimulationTest {
	/*
	Increases your total Intellect by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void intellect_is_increased(int rank) {
		assertIntellectIsIncreasedByPct(ARCANE_MIND, rank, 3 * rank);
	}
}
