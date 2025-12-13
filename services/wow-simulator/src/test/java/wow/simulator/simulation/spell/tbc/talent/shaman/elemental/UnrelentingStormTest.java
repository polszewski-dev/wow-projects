package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.TalentNames.UNRELENTING_STORM;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class UnrelentingStormTest extends TbcShamanTalentSimulationTest {
	/*
	Regenerate mana equal to 10% of your Intellect every 5 sec, even while casting.
	 */

	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3, 4, 5})
	void mp5_is_increased(int rank) {
		assertStatConversion(UNRELENTING_STORM, rank, StatSummary::getIntellect, StatSummary::getInterruptedManaRegen, 2 * rank);
	}
}
