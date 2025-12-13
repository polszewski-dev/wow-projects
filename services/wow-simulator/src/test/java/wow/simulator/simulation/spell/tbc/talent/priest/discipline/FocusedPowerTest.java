package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.FOCUSED_POWER;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class FocusedPowerTest extends TbcPriestTalentSimulationTest {
	/*
	Your Smite, Mind Blast and Mass Dispel spells have an additional 4% chance to hit.  In addition, your Mass Dispel cast time is reduced by 1 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void smite_hit_chance_is_increased(int rank) {
		simulateTalent(FOCUSED_POWER, rank, SMITE);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void mind_blast_hit_chance_is_increased(int rank) {
		simulateTalent(FOCUSED_POWER, rank, MIND_BLAST);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}
}
