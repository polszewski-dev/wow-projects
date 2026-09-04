package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.TalentNames.SPIRITUAL_GUIDANCE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SpiritualGuidanceTest extends TbcPriestTalentSimulationTest {
	/*
	Increases spell damage and healing by up to 25% of your total Spirit.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void spell_power_is_increased(int rank) {
		enableTalent(SPIRITUAL_GUIDANCE, rank);

		assertSpiritToSpellPowerConversion(player, 5 * rank);
	}
}
