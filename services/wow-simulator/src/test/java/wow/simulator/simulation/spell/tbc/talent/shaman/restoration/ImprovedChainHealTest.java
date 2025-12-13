package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.CHAIN_HEAL;
import static wow.test.commons.TalentNames.IMPROVED_CHAIN_HEAL;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedChainHealTest extends TbcShamanTalentSimulationTest {
	/*
	Increases the amount healed by your Chain Heal spell by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healing_is_increased(int rank) {
		simulateTalent(IMPROVED_CHAIN_HEAL, rank, CHAIN_HEAL);

		assertHealingIsIncreasedByPct(10 * rank);
	}
}
