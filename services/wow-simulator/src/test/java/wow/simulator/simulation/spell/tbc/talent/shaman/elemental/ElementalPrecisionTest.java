package wow.simulator.simulation.spell.tbc.talent.shaman.elemental;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.shaman.TbcShamanTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.ELEMENTAL_PRECISION;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ElementalPrecisionTest extends TbcShamanTalentSimulationTest {
	/*
	Increases your chance to hit with Fire, Frost and Nature spells by 6% and reduces the threat caused by Fire, Frost and Nature spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void fire_hit_chance_is_increased(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, FLAME_SHOCK);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void frost_hit_chance_is_increased(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, FROST_SHOCK);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void nature_hit_chance_is_increased(int rank) {
		simulateTalent(ELEMENTAL_PRECISION, rank, EARTH_SHOCK);

		assertHitChanceIsIncreasedByPct(2 * rank);
	}
}
