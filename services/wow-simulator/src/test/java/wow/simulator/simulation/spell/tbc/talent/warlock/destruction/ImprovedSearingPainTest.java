package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.SEARING_PAIN;
import static wow.test.commons.TalentNames.IMPROVED_SEARING_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ImprovedSearingPainTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the critical strike chance of your Searing Pain spell by 10%.
	 */

	@ParameterizedTest
	@CsvSource({
			"1, 4",
			"2, 7",
			"3, 10"
	})
	void spellCritBonus(int rank, int expectedPct) {
		var spellCritPctBefore = player.getStats().getSpellCritPct();

		enableTalent(IMPROVED_SEARING_PAIN, rank);

		player.cast(SEARING_PAIN);

		updateUntil(30);

		assertLastCritChance(spellCritPctBefore + expectedPct);
	}
}
