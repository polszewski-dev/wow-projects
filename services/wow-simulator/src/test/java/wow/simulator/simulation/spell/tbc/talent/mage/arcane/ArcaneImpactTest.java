package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.ARCANE_BLAST;
import static wow.test.commons.AbilityNames.ARCANE_EXPLOSION;
import static wow.test.commons.TalentNames.ARCANE_IMPACT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ArcaneImpactTest extends TbcMageTalentSimulationTest {
	/*
	Increases the critical strike chance of your Arcane Explosion and Arcane Blast spells by an additional 6%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void arcane_explosion_crit_chance_is_increased(int rank) {
		simulateTalent(ARCANE_IMPACT, rank, ARCANE_EXPLOSION);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void arcane_blast_crit_chance_is_increased(int rank) {
		simulateTalent(ARCANE_IMPACT, rank, ARCANE_BLAST);

		assertCritChanceIsIncreasedByPct(2 * rank);
	}
}
