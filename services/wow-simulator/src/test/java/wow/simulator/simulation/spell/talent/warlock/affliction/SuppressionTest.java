package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.talent.TalentId.SUPPRESSION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SuppressionTest extends WarlockSpellSimulationTest {
	/*
	Reduces the chance for enemies to resist your Affliction spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void hitChanceIsIncreasedByTwoPointsPerRank(int rank) {
		enableTalent(SUPPRESSION, rank);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertLastHitChance(83 + 2 * rank);
	}
}
