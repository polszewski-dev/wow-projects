package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.PYROMANIAC;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class PyromaniacTest extends TbcMageTalentSimulationTest {
	/*
	Increases chance to critically hit and reduces the mana cost of all Fire spells by an additional 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(PYROMANIAC, rank, FIREBALL);

		assertCritChanceIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void mana_cost_is_reduced(int rank) {
		simulateTalent(PYROMANIAC, rank, FIREBALL);

		assertManaCostIsReducedByPct(rank);
	}
}
