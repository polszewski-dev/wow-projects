package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.CONCUSSION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ConcussionTest extends TbcShamanTalentSimulationTest {
	/*
	Increases the damage done by your Lightning Bolt, Chain Lightning and Shock spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void lightning_bolt_damage_is_increased(int rank) {
		simulateTalent(CONCUSSION, rank, LIGHTNING_BOLT);

		assertDamageIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void chain_lightning_damage_is_increased(int rank) {
		simulateTalent(CONCUSSION, rank, CHAIN_LIGHTNING);

		assertDamageIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void earth_shock_damage_is_increased(int rank) {
		simulateTalent(CONCUSSION, rank, EARTH_SHOCK);

		assertDamageIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void flame_shock_damage_is_increased(int rank) {
		simulateTalent(CONCUSSION, rank, FLAME_SHOCK);

		assertDamageIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void frost_shock_damage_is_increased(int rank) {
		simulateTalent(CONCUSSION, rank, FROST_SHOCK);

		assertDamageIsIncreasedByPct(rank);
	}
}
