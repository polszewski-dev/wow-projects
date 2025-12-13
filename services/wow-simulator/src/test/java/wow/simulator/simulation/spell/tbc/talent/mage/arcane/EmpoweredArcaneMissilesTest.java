package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.ARCANE_MISSILES;
import static wow.test.commons.TalentNames.EMPOWERED_ARCANE_MISSILES;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class EmpoweredArcaneMissilesTest extends TbcMageTalentSimulationTest {
	/*
	Your Arcane Missiles spell gains an additional 45% of your bonus spell damage effects, but mana cost is increased by 6%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spell_coefficient_is_increased(int rank) {
		simulateTalent(EMPOWERED_ARCANE_MISSILES, rank, ARCANE_MISSILES);

		assertDamageCoefficientIsIncreasedBy(15 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mana_cost_is_reduced(int rank) {
		simulateTalent(EMPOWERED_ARCANE_MISSILES, rank, ARCANE_MISSILES);

		assertManaCostIsIncreasedByPct(2 * rank);
	}
}
