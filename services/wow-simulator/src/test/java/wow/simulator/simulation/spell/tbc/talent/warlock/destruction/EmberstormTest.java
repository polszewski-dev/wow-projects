package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.INCINERATE;
import static wow.test.commons.TalentNames.EMBERSTORM;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class EmberstormTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the damage done by your Fire spells by 10%, and reduces the cast time of your Incinerate spell by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIncreased(int rank) {
		enableTalent(EMBERSTORM, rank);

		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(INCINERATE, INCINERATE_INFO.damage(), 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void castTimeReduced(int rank) {
		enableTalent(EMBERSTORM, rank);

		player.cast(INCINERATE);

		updateUntil(30);

		assertCastTime(INCINERATE, INCINERATE_INFO.baseCastTime(), -2 * rank);
	}
}
