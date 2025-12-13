package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.HEALING_TOUCH;
import static wow.test.commons.AbilityNames.TRANQUILITY;
import static wow.test.commons.TalentNames.TRANQUIL_SPIRIT;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class TranquilSpiritTest extends TbcDruidTalentSimulationTest {
	/*
	Reduces the mana cost of your Healing Touch and Tranquility spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void healing_touch_mana_cost_is_reduced(int rank) {
		simulateTalent(TRANQUIL_SPIRIT, rank, HEALING_TOUCH);

		assertManaCostIsReducedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void tranquility_mana_cost_is_reduced(int rank) {
		simulateTalent(TRANQUIL_SPIRIT, rank, TRANQUILITY);

		assertManaCostIsReducedByPct(2 * rank);
	}
}
