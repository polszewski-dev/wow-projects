package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIRE_BLAST;
import static wow.test.commons.AbilityNames.SCORCH;
import static wow.test.commons.TalentNames.INCINERATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class IncinerationTest extends TbcMageTalentSimulationTest {
	/*
	Increases the critical strike chance of your Fire Blast and Scorch spells by 4%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void fire_blast_crit_chance_is_increased(int rank) {
		simulateTalent(INCINERATION, rank, FIRE_BLAST);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void scorch_crit_chance_is_increased(int rank) {
		simulateTalent(INCINERATION, rank, SCORCH);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}
}
