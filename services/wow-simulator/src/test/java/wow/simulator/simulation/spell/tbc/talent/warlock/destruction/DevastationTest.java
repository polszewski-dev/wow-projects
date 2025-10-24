package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.DEVASTATION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DevastationTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the critical strike chance of your Destruction spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spellCritBonus(int rank) {
		var spellCritPctBefore = player.getStats().getSpellCritPct();

		enableTalent(DEVASTATION, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertLastCritChance(spellCritPctBefore + rank);
	}
}
