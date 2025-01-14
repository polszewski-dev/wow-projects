package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.talent.TalentId.BACKLASH;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class BacklashTest extends WarlockSpellSimulationTest {
	/*
	Increases your critical strike chance with spells by an additional 3% and gives you a 25% chance when hit by a physical attack to reduce the cast time
	of your next Shadow Bolt or Incinerate spell by 100%. This effect lasts 8 sec and will not occur more than once every 8 seconds.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spellCritBonus(int rank) {
		var spellCritPctBefore = player.getStats().getSpellCritPct();

		enableTalent(BACKLASH, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertLastCritChance(spellCritPctBefore + rank);
	}
}
