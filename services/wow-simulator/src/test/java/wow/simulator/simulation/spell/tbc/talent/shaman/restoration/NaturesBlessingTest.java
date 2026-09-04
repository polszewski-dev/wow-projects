package wow.simulator.simulation.spell.tbc.talent.shaman.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.TalentNames.NATURES_BLESSING;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class NaturesBlessingTest extends TbcShamanTalentSimulationTest {
	/*
	Increases your spell damage and healing by an amount equal to 30% of your Intellect.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spell_power_is_increased(int rank) {
		enableTalent(NATURES_BLESSING, rank);

		assertIntellectToSpellPowerConversion(player, 10 * rank);
	}
}
