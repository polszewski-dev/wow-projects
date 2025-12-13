package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.CHAIN_LIGHTNING;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;
import static wow.test.commons.TalentNames.LIGHTNING_MASTERY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class LightningMasteryTest extends TbcShamanTalentSimulationTest {
	/*
	Reduces the cast time of your Lightning Bolt and Chain Lightning spells by 0.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void lightning_bolt_cast_time_is_reduced(int rank) {
		simulateTalent(LIGHTNING_MASTERY, rank, LIGHTNING_BOLT);

		assertCastTimeIsReducedBy(0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void chain_lightning_cast_time_is_reduced(int rank) {
		simulateTalent(LIGHTNING_MASTERY, rank, CHAIN_LIGHTNING);

		assertCastTimeIsReducedBy(0.1 * rank);
	}
}
