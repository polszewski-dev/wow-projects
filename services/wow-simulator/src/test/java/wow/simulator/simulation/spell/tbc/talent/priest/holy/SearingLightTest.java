package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.HOLY_FIRE;
import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.SEARING_LIGHT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SearingLightTest extends TbcPriestTalentSimulationTest {
	/*
	Increases the damage of your Smite and Holy Fire spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void smite_damage_is_increased(int rank) {
		simulateTalent(SEARING_LIGHT, rank, SMITE);

		assertDamageIsIncreasedByPct(5 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void holy_fire_damage_is_increased(int rank) {
		simulateTalent(SEARING_LIGHT, rank, HOLY_FIRE);

		assertDamageIsIncreasedByPct(5 * rank);
	}
}
