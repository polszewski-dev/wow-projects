package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.CATACLYSM;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class CataclysmTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the Mana cost of your Destruction spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void mana_cost_is_reduced(int rank) {
		simulateTalent(CATACLYSM, rank, SHADOW_BOLT);

		assertManaCostIsReducedByPct(rank);
	}
}
