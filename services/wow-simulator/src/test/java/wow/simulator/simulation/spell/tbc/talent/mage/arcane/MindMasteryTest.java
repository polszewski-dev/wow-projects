package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.TalentNames.MIND_MASTERY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class MindMasteryTest extends TbcMageTalentSimulationTest {
	/*
	Increases spell damage by up to 25% of your total Intellect.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spell_damage_is_increased(int rank) {
		assertStatConversion(MIND_MASTERY, rank, StatSummary::getIntellect, StatSummary::getSpellDamage, 5 * rank);
	}
}
