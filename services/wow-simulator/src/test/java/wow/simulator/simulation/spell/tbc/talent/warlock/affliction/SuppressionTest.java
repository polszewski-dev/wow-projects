package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.TalentNames.SUPPRESSION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SuppressionTest extends TbcWarlockTalentSimulationTest {
	/*
	Reduces the chance for enemies to resist your Affliction spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void hit_chance_is_increased(int rank) {
		simulateTalent(SUPPRESSION, rank, CORRUPTION);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}
}
