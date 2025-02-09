package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHOOT;
import static wow.commons.model.talent.TalentId.WAND_SPECIALIZATION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class WandSpecializationTest extends PriestSpellSimulationTest {
	/*
	Increases your damage with Wands by 25%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIsIncreased(int rank) {
		enableTalent(WAND_SPECIALIZATION, rank);
		equip("Wand of the Demonsoul");// 208 - 387 dmg, 1.5 speed

		player.cast(SHOOT);

		updateUntil(30);

		assertDamageDone(SHOOT, SHOOT_INFO.withDirect(208, 387, 0).damage(), 5 * rank);
	}
}
