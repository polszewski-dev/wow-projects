package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_WORD_PAIN;
import static wow.test.commons.TalentNames.MENTAL_AGILITY;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class MentalAgilityTest extends TbcPriestTalentSimulationTest {
	/*
	Reduces the mana cost of your instant cast spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void mana_cost_is_reduced(int rank) {
		simulateTalent(MENTAL_AGILITY, rank, SHADOW_WORD_PAIN);

		assertManaCostIsReducedByPct(2 * rank);
	}
}
