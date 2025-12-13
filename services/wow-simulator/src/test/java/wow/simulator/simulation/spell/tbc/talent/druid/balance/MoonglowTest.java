package wow.simulator.simulation.spell.tbc.talent.druid.balance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.MOONGLOW;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class MoonglowTest extends TbcDruidTalentSimulationTest {
	/*
	Reduces the Mana cost of your Moonfire, Starfire, Wrath, Healing Touch, Regrowth and Rejuvenation spells by 9%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void moonfire_mana_cost_is_reduced(int rank) {
		simulateTalent(MOONGLOW, rank, MOONFIRE);

		assertManaCostIsReducedByPct(3 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void starfire_mana_cost_is_reduced(int rank) {
		simulateTalent(MOONGLOW, rank, STARFIRE);

		assertManaCostIsReducedByPct(3 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void wrath_mana_cost_is_reduced(int rank) {
		simulateTalent(MOONGLOW, rank, WRATH);

		assertManaCostIsReducedByPct(3 * rank);
	}
}
