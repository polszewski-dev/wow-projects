package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.MIND_BLAST;
import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.FOCUSED_POWER;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class FocusedPowerTest extends TbcPriestSpellSimulationTest {
	/*
	Your Smite, Mind Blast and Mass Dispel spells have an additional 4% chance to hit.  In addition, your Mass Dispel cast time is reduced by 1 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void smiteHitChanceIsIncreased(int rank) {
		enableTalent(FOCUSED_POWER, rank);

		player.cast(SMITE);

		updateUntil(30);

		assertLastHitChance(83 + 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void mindBlastHitChanceIsIncreased(int rank) {
		enableTalent(FOCUSED_POWER, rank);

		player.cast(MIND_BLAST);

		updateUntil(30);

		assertLastHitChance(83 + 2 * rank);
	}
}
