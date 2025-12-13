package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.INCINERATE;
import static wow.test.commons.TalentNames.EMBERSTORM;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class EmberstormTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the damage done by your Fire spells by 10%, and reduces the cast time of your Incinerate spell by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damage_is_increased(int rank) {
		simulateTalent(EMBERSTORM, rank, INCINERATE);

		assertDamageIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void cast_time_is_reduced(int rank) {
		simulateTalent(EMBERSTORM, rank, INCINERATE);

		assertCastTimeIsReducedByPct(2 * rank);
	}
}
