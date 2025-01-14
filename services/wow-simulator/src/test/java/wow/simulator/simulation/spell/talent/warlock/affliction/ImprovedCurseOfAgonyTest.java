package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CURSE_OF_AGONY;
import static wow.commons.model.talent.TalentId.IMPROVED_CURSE_OF_AGONY;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class ImprovedCurseOfAgonyTest extends WarlockSpellSimulationTest {
	/*
	Increases the damage done by your Curse of Agony by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void improvedCurseOfAgony(int rank) {
		enableTalent(IMPROVED_CURSE_OF_AGONY, rank);

		player.cast(CURSE_OF_AGONY);

		updateUntil(30);

		assertDamageDone(CURSE_OF_AGONY, 1356, 5 * rank);
	}
}
