package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.test.commons.AbilityNames.HOLY_LIGHT;
import static wow.test.commons.TalentNames.SANCTIFIED_LIGHT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class SanctifiedLightTest extends TbcPaladinTalentSimulationTest {
	/*
	Increases the critical effect chance of your Holy Light spell by 6%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(SANCTIFIED_LIGHT, rank, HOLY_LIGHT);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}
}
