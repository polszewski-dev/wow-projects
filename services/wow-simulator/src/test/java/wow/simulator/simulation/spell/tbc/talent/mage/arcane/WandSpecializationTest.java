package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHOOT;
import static wow.test.commons.TalentNames.WAND_SPECIALIZATION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class WandSpecializationTest extends TbcMageTalentSimulationTest {
	/*
	Increases your damage with Wands by 25%.
	 */

	@ParameterizedTest
	@CsvSource({
			"1, 13",
			"2, 25"
	})
	void damage_is_increased(int rank, int pctIncrease) {
		equip(player, "Wand of the Demonsoul");
		equip(player2, "Wand of the Demonsoul");

		simulateTalent(WAND_SPECIALIZATION, rank, SHOOT);

		assertDamageIsIncreasedByPct(pctIncrease);
	}
}
