package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.TalentNames.SHADOW_MASTERY;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShadowMasteryTest extends WarlockSpellSimulationTest {
	/*
	Increases the damage dealt or life drained by your Shadow spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIsIncreased(int rank) {
		enableTalent(SHADOW_MASTERY, rank);

		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(), 2 * rank);
	}
}
