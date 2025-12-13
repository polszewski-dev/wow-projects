package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_WAVE;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;
import static wow.test.commons.TalentNames.TIDAL_MASTERY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class TidalMasteryTest extends TbcShamanTalentSimulationTest {
	/*
	Increases the critical effect chance of your healing and lightning spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void lightning_spell_crit_chance_is_increased(int rank) {
		simulateTalent(TIDAL_MASTERY, rank, LIGHTNING_BOLT);

		assertCritChanceIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void healing_spell_crit_chance_is_increased(int rank) {
		simulateTalent(TIDAL_MASTERY, rank, HEALING_WAVE);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
