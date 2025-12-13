package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.CONE_OF_COLD;
import static wow.test.commons.TalentNames.ICE_FLOES;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class IceFloesTest extends TbcMageTalentSimulationTest {
	/*
	Reduces the cooldown of your Cone of Cold, Cold Snap, Ice Barrier and Ice Block spells by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void cone_of_cold_cooldown_is_reduced(int rank) {
		simulateTalent(ICE_FLOES, rank, CONE_OF_COLD);

		assertCooldownIsReducedByPct(10 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void cold_snap_cooldown_is_reduced(int rank) {
		simulateTalent(ICE_FLOES, rank, CONE_OF_COLD);

		assertCooldownIsReducedByPct(10 * rank);
	}
}
