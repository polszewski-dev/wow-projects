package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.REVERBERATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ReverberationTest extends TbcShamanTalentSimulationTest {
	/*
	Reduces the cooldown of your Shock spells by 1 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void frost_shock_cooldown_is_reduced(int rank) {
		simulateTalent(REVERBERATION, rank, FROST_SHOCK);

		assertCooldownIsReducedBy(0.2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void flame_shock_cooldown_is_reduced(int rank) {
		simulateTalent(REVERBERATION, rank, FLAME_SHOCK);

		assertCooldownIsReducedBy(0.2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void earth_shock_cooldown_is_reduced(int rank) {
		simulateTalent(REVERBERATION, rank, EARTH_SHOCK);

		assertCooldownIsReducedBy(0.2 * rank);
	}
}
