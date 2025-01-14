package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.INCINERATE;
import static wow.commons.model.talent.TalentId.EMBERSTORM;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class EmberstormTest extends WarlockSpellSimulationTest {
	/*
	Increases the damage done by your Fire spells by 10%, and reduces the cast time of your Incinerate spell by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIncreased(int rank) {
		enableTalent(EMBERSTORM, rank);

		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(INCINERATE, 479, 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void castTimeReduced(int rank) {
		enableTalent(EMBERSTORM, rank);

		player.cast(INCINERATE);

		updateUntil(30);

		assertCastTime(INCINERATE, 2.5, -2 * rank);
	}
}
