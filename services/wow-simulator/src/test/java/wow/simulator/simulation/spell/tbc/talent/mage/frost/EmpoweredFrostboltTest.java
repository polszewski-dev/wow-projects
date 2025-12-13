package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.EMPOWERED_FROSTBOLT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class EmpoweredFrostboltTest extends TbcMageTalentSimulationTest {
	/*
	Your Frostbolt spell gains an additional 10% of your bonus spell damage effects and an additional 5% chance to critically strike.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spell_coefficient_is_increased(int rank) {
		simulateTalent(EMPOWERED_FROSTBOLT, rank, FROSTBOLT);

		assertDamageCoefficientIsIncreasedBy(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(EMPOWERED_FROSTBOLT, rank, FROSTBOLT);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
