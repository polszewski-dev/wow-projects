package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.CONVECTION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ConvectionTest extends TbcShamanTalentSimulationTest {
	/*
	Reduces the mana cost of your Shock, Lightning Bolt and Chain Lightning spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void lightning_bolt_mana_cost_is_reduced(int rank) {
		simulateTalent(CONVECTION, rank, LIGHTNING_BOLT);

		assertManaCostIsReducedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void chain_lightning_mana_cost_is_reduced(int rank) {
		simulateTalent(CONVECTION, rank, CHAIN_LIGHTNING);

		assertManaCostIsReducedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void earth_shock_mana_cost_is_reduced(int rank) {
		simulateTalent(CONVECTION, rank, EARTH_SHOCK);

		assertManaCostIsReducedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void flame_shock_mana_cost_is_reduced(int rank) {
		simulateTalent(CONVECTION, rank, FLAME_SHOCK);

		assertManaCostIsReducedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void frost_shock_mana_cost_is_reduced(int rank) {
		simulateTalent(CONVECTION, rank, FROST_SHOCK);

		assertManaCostIsReducedByPct(2 * rank);
	}
}
