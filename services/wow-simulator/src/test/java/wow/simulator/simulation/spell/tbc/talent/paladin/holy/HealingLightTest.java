package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.test.commons.AbilityNames.FLASH_OF_LIGHT;
import static wow.test.commons.AbilityNames.HOLY_LIGHT;
import static wow.test.commons.TalentNames.HEALING_LIGHT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class HealingLightTest extends TbcPaladinTalentSimulationTest {
	/*
	Increases the amount healed by your Holy Light and Flash of Light spells by 12%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void holy_light_healing_is_increased(int rank) {
		simulateTalent(HEALING_LIGHT, rank, HOLY_LIGHT);

		assertHealingIsIncreasedByPct(4 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void flash_of_light_healing_is_increased(int rank) {
		simulateTalent(HEALING_LIGHT, rank, FLASH_OF_LIGHT);

		assertHealingIsIncreasedByPct(4 * rank);
	}
}
