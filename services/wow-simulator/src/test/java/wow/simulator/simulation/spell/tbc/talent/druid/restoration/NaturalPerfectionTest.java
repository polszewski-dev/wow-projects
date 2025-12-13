package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.WRATH;
import static wow.test.commons.TalentNames.NATURAL_PERFECTION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class NaturalPerfectionTest extends TbcDruidTalentSimulationTest {
	/*
	Your critical strike chance with all spells is increased by 3%
	and critical strikes against you give you the Natural Perfection effect
	reducing all damage taken by 4%. Stacks up to 3 times. Lasts 8 sec.
	*/

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(NATURAL_PERFECTION, rank, WRATH);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
