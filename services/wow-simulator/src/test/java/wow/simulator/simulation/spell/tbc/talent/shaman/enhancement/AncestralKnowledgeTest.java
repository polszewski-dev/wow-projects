package wow.simulator.simulation.spell.tbc.talent.shaman.enhancement;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.TalentNames.ANCESTRAL_KNOWLEDGE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class AncestralKnowledgeTest extends TbcShamanTalentSimulationTest {
	/*
	Increases your maximum mana by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void max_mana_is_increased(int rank) {
		assertMaxManaIsIncreasedByPct(ANCESTRAL_KNOWLEDGE, rank, rank);
	}
}
