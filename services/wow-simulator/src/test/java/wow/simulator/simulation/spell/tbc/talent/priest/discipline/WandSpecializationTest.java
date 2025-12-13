package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.SHOOT;
import static wow.test.commons.TalentNames.WAND_SPECIALIZATION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class WandSpecializationTest extends TbcPriestTalentSimulationTest {
	/*
	Increases your damage with Wands by 25%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damage_is_increased(int rank) {
		equip(player, "Wand of the Demonsoul");
		equip(player2, "Wand of the Demonsoul");

		simulateTalent(WAND_SPECIALIZATION, rank, SHOOT);

		assertDamageIsIncreasedByPct(5 * rank);
	}
}
