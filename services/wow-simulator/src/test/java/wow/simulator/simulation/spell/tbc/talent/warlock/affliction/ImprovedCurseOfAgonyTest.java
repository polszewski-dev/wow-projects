package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.TalentNames.IMPROVED_CURSE_OF_AGONY;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCurseOfAgonyTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the damage done by your Curse of Agony by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damage_is_increased(int rank) {
		simulateTalent(IMPROVED_CURSE_OF_AGONY, rank, CURSE_OF_AGONY);

		assertDamageIsIncreasedByPct(5 * rank);
	}
}
