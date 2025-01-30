package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.talent.TalentId.IMPROVED_IMMOLATE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedImmolateTest extends WarlockSpellSimulationTest {
	/*
	Increases the initial damage of your Immolate spell by 25%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void improvedImmolate(int rank) {
		enableTalent(IMPROVED_IMMOLATE, rank);

		player.cast(IMMOLATE);

		updateUntil(30);

		assertDamageDone(0, IMMOLATE, IMMOLATE_INFO.direct().damage(0, 0), 5 * rank);

		for (int tickNo = 0; tickNo < IMMOLATE_INFO.numTicks(); ++tickNo) {
			assertDamageDone(tickNo + 1, IMMOLATE, IMMOLATE_INFO.tickDamage());
		}
	}
}
