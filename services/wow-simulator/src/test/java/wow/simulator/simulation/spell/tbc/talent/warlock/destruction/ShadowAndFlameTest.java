package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.INCINERATE;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.SHADOW_AND_FLAME;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShadowAndFlameTest extends TbcWarlockTalentSimulationTest {
	/*
	Your Shadow Bolt and Incinerate spells gain an additional 20% of your bonus spell damage effects.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void sb_spell_coefficient_is_increased(int rank) {
		simulateTalent(SHADOW_AND_FLAME, rank, SHADOW_BOLT);

		assertDamageCoefficientIsIncreasedBy(4 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void incinerate_spell_coefficient_is_increased(int rank) {
		simulateTalent(SHADOW_AND_FLAME, rank, INCINERATE);

		assertDamageCoefficientIsIncreasedBy(4 * rank);
	}
}
