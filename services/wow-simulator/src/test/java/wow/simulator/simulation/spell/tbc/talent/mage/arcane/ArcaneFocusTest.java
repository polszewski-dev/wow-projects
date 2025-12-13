package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.ARCANE_MISSILES;
import static wow.test.commons.TalentNames.ARCANE_FOCUS;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ArcaneFocusTest extends TbcMageTalentSimulationTest {
	/*
	Reduces the chance that the opponent can resist your Arcane spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void hit_chance_is_increased(int rank) {
		simulateTalent(ARCANE_FOCUS, rank, ARCANE_MISSILES);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}
}
