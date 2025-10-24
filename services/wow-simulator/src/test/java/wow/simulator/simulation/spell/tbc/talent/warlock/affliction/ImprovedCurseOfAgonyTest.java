package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.TalentNames.IMPROVED_CURSE_OF_AGONY;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCurseOfAgonyTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the damage done by your Curse of Agony by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void improvedCurseOfAgony(int rank) {
		enableTalent(IMPROVED_CURSE_OF_AGONY, rank);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertDamageDone(CURSE_OF_AGONY, CURSE_OF_AGONY_INFO.damage(), 5 * rank);
	}
}
