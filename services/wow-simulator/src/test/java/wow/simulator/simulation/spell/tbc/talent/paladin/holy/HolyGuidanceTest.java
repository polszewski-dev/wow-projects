package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.test.commons.TalentNames.HOLY_GUIDANCE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class HolyGuidanceTest extends TbcPaladinTalentSimulationTest {
	/*
	Increases your spell damage and healing by 35% of your total Intellect.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spell_power_is_increased(int rank) {
		assertStatConversion(HOLY_GUIDANCE, rank, StatSummary::getIntellect, StatSummary::getSpellPower, 7 * rank);
	}
}
