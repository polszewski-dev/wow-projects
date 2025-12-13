package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.FROST_CHANNELING;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class FrostChannelingTest extends TbcMageTalentSimulationTest {
	/*
	Reduces the mana cost of your Frost spells by 15% and reduces the threat caused by your Frost spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mana_cost_is_reduced(int rank) {
		simulateTalent(FROST_CHANNELING, rank, FROSTBOLT);

		assertManaCostIsReducedByPct(5 * rank);
	}
}
