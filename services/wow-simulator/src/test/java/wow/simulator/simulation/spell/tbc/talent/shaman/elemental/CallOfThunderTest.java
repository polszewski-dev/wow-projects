package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.CHAIN_LIGHTNING;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;
import static wow.test.commons.TalentNames.CALL_OF_THUNDER;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class CallOfThunderTest extends TbcShamanTalentSimulationTest {
	/*
	Increases the critical strike chance of your Lightning Bolt and Chain Lightning spells by an additional 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void lightning_bolt_crit_chance_is_increased(int rank) {
		simulateTalent(CALL_OF_THUNDER, rank, LIGHTNING_BOLT);

		assertCritChanceIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void chain_lightning_crit_chance_is_increased(int rank) {
		simulateTalent(CALL_OF_THUNDER, rank, CHAIN_LIGHTNING);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
