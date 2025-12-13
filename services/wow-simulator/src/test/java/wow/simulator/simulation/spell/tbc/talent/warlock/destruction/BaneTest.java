package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.BANE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class BaneTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the casting time of your Shadow Bolt and Immolate spells by 0.5 sec and your Soul Fire spell by 2 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void sb_cast_time_is_reduced(int rank) {
		simulateTalent(BANE, rank, SHADOW_BOLT);

		assertCastTimeIsReducedBy(0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void immolate_cast_time_is_reduced(int rank) {
		simulateTalent(BANE, rank, IMMOLATE);

		assertCastTimeIsReducedBy(0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void soul_fire_cast_time_is_reduced(int rank) {
		simulateTalent(BANE, rank, SOUL_FIRE);

		assertCastTimeIsReducedBy(0.4 * rank);
	}
}
