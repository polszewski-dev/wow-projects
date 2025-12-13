package wow.simulator.simulation.spell.tbc.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SHADOW_WORD_DEATH;
import static wow.test.commons.TalentNames.SHADOW_POWER;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ShadowPowerTest extends TbcPriestTalentSimulationTest {
	/*
	Increases the critical strike chance of your Mind Blast and Shadow Word: Death spells by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void mind_blast_crit_chance_is_increased(int rank) {
		simulateTalent(SHADOW_POWER, rank, MIND_BLAST);

		assertCritChanceIsIncreasedByPct(3 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void sw_death_crit_chance_is_increased(int rank) {
		simulateTalent(SHADOW_POWER, rank, SHADOW_WORD_DEATH);

		assertCritChanceIsIncreasedByPct(3 * rank);
	}
}
