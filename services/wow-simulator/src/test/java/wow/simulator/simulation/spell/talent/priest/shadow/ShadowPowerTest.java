package wow.simulator.simulation.spell.talent.priest.shadow;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SHADOW_WORD_DEATH;
import static wow.test.commons.TalentNames.SHADOW_POWER;

/**
 * User: POlszewski
 * Date: 2025-01-17
 */
class ShadowPowerTest extends PriestSpellSimulationTest {
	/*
	Increases the critical strike chance of your Mind Blast and Shadow Word: Death spells by 15%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void mindBlastCritChanceIsIncreased(int rank) {
		var spellCritPctBefore = player.getStats().getSpellCritPct();

		critsOnlyOnFollowingRolls(0);
		enableTalent(SHADOW_POWER, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertLastCritChance(spellCritPctBefore + 3 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void shadowWordDeathCritChanceIsIncreased(int rank) {
		var spellCritPctBefore = player.getStats().getSpellCritPct();

		critsOnlyOnFollowingRolls(0);
		enableTalent(SHADOW_POWER, rank);

		player.cast(SHADOW_WORD_DEATH);

		updateUntil(30);

		assertLastCritChance(spellCritPctBefore + 3 * rank);
	}
}
