package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.DIVINE_FURY;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DivineFuryTest extends TbcPriestTalentSimulationTest {
	/*
	Reduces the casting time of your Smite, Holy Fire, Heal and Greater Heal spells by 0.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void smite_cast_time_is_reduced(int rank) {
		simulateTalent(DIVINE_FURY, rank, SMITE);

		assertCastTimeIsReducedBy(0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void holy_fire_cast_time_is_reduced(int rank) {
		simulateTalent(DIVINE_FURY, rank, HOLY_FIRE);

		assertCastTimeIsReducedBy(0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void greater_heal_cast_time_is_reduced(int rank) {
		simulateTalent(DIVINE_FURY, rank, GREATER_HEAL);

		assertCastTimeIsReducedBy(0.1 * rank);
	}
}
