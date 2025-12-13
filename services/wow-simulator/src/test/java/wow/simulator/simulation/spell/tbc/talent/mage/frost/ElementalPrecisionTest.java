package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.ELEMENTAL_PRECISION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ElementalPrecisionTest extends TbcMageTalentSimulationTest {
	/*
	Reduces the mana cost and chance targets resist your Frost and Fire spells by 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void hit_chance_of_frost_spells_is_increased(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, FROSTBOLT);

		assertHitChanceIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void hit_chance_of_fire_spells_is_increased(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, FIREBALL);

		assertHitChanceIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mana_cost_of_frost_spells_is_reduced(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, FROSTBOLT);

		assertManaCostIsReducedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mana_cost_of_fire_spells_is_reduced(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, FIREBALL);

		assertManaCostIsReducedByPct(rank);
	}
}
